package routes

import cats.effect.IO
import cats.implicits._
import clients.sql.PostgresClient
import clients.steam.SteamClient
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

// @formatter:off
class InventoryRoutes(pgClient: PostgresClient, steamClient: SteamClient) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    
    // source remote inventory, validate, and write to local
    case GET -> Root / "inventory" / "refresh" / steamId / IntVar(count) =>
      for {
        refreshId <- java.util.UUID.randomUUID().pure[IO]
        assets    <- steamClient.sourceAssets(refreshId, steamId, count)
        _         <- pgClient.insertAssets(assets)
        response  <- Ok("success")
      } yield response
    
    // change the state of an asset to `trading` || `nottrading`
    // todo add UUID of asset as query param  
    case PUT -> Root / "asset" :? UuidToQPM(uuidValidated) +& StateToQPM(statetoValidated) =>
      statetoValidated.fold(
        parseFailure => BadRequest(parseFailure.head.sanitized),
        stateto      => Ok(stateto.toString + uuidValidated.toString)
      )
      
      

  }

}
