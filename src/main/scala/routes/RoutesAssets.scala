package routes

import cats.effect._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import types.{ResourcesService, _}

class RoutesAssets(resources: ResourcesService)(implicit C: Clock[IO]) extends Http4sDsl[IO] {
  
  import resources._
  
  implicit def logger = Slf4jLogger.getLogger[IO]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "assets" / steamId => getAssets.run(IdSteam(steamId), Count(1000))
                                                                    
//    case GET -> Root / "assets" / steamId :? InventoryActionQPM(actionValidated) +& CountQPM(countValidated) =>
//      (actionValidated, countValidated)
//        .mapN((action, count) =>
//          action match {
//            case ActionAssets.Get => getAssets.run(steamId, count)
//          }
//        )
//        .valueOr(errors => BadRequest(errors.show))

//    case PUT -> Root / "asset" :? AssetIdQPM(assetIdValidated) +& TradingQPM(tradingValidated) =>
//      (assetIdValidated, tradingValidated)
//        .mapN((assetId, trading) => 
//          for {
//            // todo determine if asset_data_b for assetid already exists. if trading true, it must not exist.  if trading false, it must exist
//            assetDataA <- clientPg.selectAsset(assetId)
//            floatValue <- clientCsFloat.getFloatValue(assetDataA.assetid)
////            _          <- pgClient.insert(AssetDataB(assetDataA.dexpress_asset_id, floatValue))
//            response   <- NoContent()
//          } yield response 
//        )
//        .valueOr(errors => BadRequest(errors.show))    
                            
                            
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
