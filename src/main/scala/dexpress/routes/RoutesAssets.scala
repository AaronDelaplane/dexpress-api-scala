package dexpress.routes

import cats.effect._
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import dexpress.codecs._
import dexpress.show._
import dexpress.types._

class RoutesAssets(resources: ResourcesService)(implicit C: Clock[IO]) extends Http4sDsl[IO] {
  
  import resources._
  
  implicit def logger = Slf4jLogger.getLogger[IO]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "assets" / steamId => toAssets.runToResponse(IdSteam(steamId))

    case PUT -> Root / "asset" :? AssetIdQPM(assetIdValidated) +& TradingQPM(tradingValidated) =>
      (assetIdValidated, tradingValidated)
        .mapN((uuid, bool) => updateAssetTradingState.run(IdAsset(uuid), StateTrading(bool)))
        .valueOr(errors    => BadRequest(errors.show))
  
  }
}
