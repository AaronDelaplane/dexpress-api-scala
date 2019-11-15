package routes

import cats.effect.IO
import cats.implicits._
import clients.sql.PostgresClient
import clients.steam.SteamClient
import clients.steam.data._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

// @formatter:off
class InventoryRoutes(pgClient: PostgresClient, steamClient: SteamClient) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    
    case GET -> Root / "inventory" / "refresh" / LongVar(steamId) =>
      for {
        assets    <- steamClient.attemptFetchAssets(steamId)
        _         <- logger.info(assets.show)           
        response  <- Ok("success")
      } yield response

  }

}
