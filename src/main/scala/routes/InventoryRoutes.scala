package routes

import java.util.UUID.randomUUID

import cats.effect.IO
import cats.implicits._
import clients.postgres.PostgresClient
import clients.steam.SteamClient
import codecs._
import enums._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import show._

/*
finish parsing of steam inventory to asset
should instance_id ever be `0`? yes 
todo set asset to `trading`. set asset to `nottrading`
todo search assets
todo refresh existing inventory
todo add refresh time limit   
 */

class InventoryRoutes(pgClient: PostgresClient, steamClient: SteamClient) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    
    // source remote inventory, validate, and write to local
    case GET -> Root / "inventory" / steamId :? InventoryActionQPM(actionValidated) +& CountQPM(countValidated) =>
      (actionValidated, countValidated)
        .mapN((action, count) =>
          action match {
            case InventoryAction.refresh => for {
               inventory  <- steamClient.getInventory(steamId, count.value)
               refreshId  <- randomUUID().pure[IO]
               assets     <- datamaps.toAssets(refreshId, steamId, inventory)
               _          <- pgClient.insert(assets)
               response   <- NoContent()
            } yield response
          }  
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
