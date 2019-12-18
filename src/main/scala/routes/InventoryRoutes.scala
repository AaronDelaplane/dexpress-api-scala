package routes

import java.util.UUID.randomUUID

import cats.effect._
import cats.implicits._
import clients.csfloat.CsFloatClient
import clients.postgres.PostgresClient
import clients.steam.SteamClient
import codecs._
import datamaps.toassetsdataa.ToAssets.toAssets
import enums._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import show._

import scala.concurrent.duration.MILLISECONDS
/*
finish parsing of steam inventory to asset
should instance_id ever be `0`? yes 
todo set asset to `trading`. set asset to `nottrading`
todo search assets
todo refresh existing inventory
todo add refresh time limit   
 */

class InventoryRoutes(pgClient: PostgresClient, steamClient: SteamClient, csFloatClient: CsFloatClient)(implicit C: Clock[IO]) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    
    // source remote inventory, validate, and write to postgres
    case GET -> Root / "inventory" / steamId :? InventoryActionQPM(actionValidated) +& CountQPM(countValidated) =>
      (actionValidated, countValidated)
        .mapN((action, count) =>
          action match {
            case InventoryAction.refresh => for {
               time        <- C.monotonic(MILLISECONDS)
               inventory   <- steamClient.getInventory(steamId, count.value)
               refreshId   <- randomUUID().pure[IO]
               assetsDataA <- toAssets(refreshId, steamId, inventory)
               _           <- pgClient.insertMany(assetsDataA, refreshId, time)
               response    <- NoContent()
            } yield response
          }  
        )
        .valueOr(errors => BadRequest(errors.show))

    case PUT -> Root / "asset" :? AssetIdQPM(assetIdValidated) +& TradingQPM(tradingValidated) =>
      (assetIdValidated, tradingValidated)
        .mapN((assetId, trading) => 
          for {
            // todo determine if asset_data_b for assetid already exists. if trading true, it must not exist.  if trading false, it must exist
            assetDataA <- pgClient.selectAsset(assetId)
            floatValue <- csFloatClient.getFloatValue(assetDataA.assetid)
//            _          <- pgClient.insert(AssetDataB(assetDataA.dexpress_asset_id, floatValue))
            response   <- NoContent()
          } yield response 
        )
        .valueOr(errors => BadRequest(errors.show))    
                            
                            
    // set state of asset to `trading` || `nottrading`. return error if state already === 
//    case PUT -> Root / "asset" :? AssetIdQPM(assetIdValidated) +& TradingQPM(tradingValidated) =>
//      (assetIdValidated, tradingValidated)
//        .mapN((assetId, trading) => 
//          for {
//            assetA   <- pgClient.selectAsset(assetId)
//            response <- if (assetA.trading === trading) BadRequest("attempt-to-update-to-existing-state")
//                        else pgClient.updateAssetTradingState(assetId, trading) *> NoContent()
//          } yield response 
//        )
//        .valueOr(errors => BadRequest(errors.show))  
  
  }
}
