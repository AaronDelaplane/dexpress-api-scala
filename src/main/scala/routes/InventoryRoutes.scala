package routes

import java.util.UUID.randomUUID

import cats.data.Validated.{Invalid, Valid}
import cats.effect.IO
import cats.implicits._
import clients.sql.PostgresClient
import clients.steam.SteamClient
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

/*
todo finish parsing of steam inventory to asset
todo should instance_id ever be `0`? 
todo set asset to `trading`. set asset to `nottrading`
todo search assets
todo refresh existing inventory
todo add refresh time limit   
 */

class InventoryRoutes(pgClient: PostgresClient, steamClient: SteamClient) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    
    // source remote inventory, validate, and write to local
    case GET -> Root / "inventory" / "refresh" / steamId / IntVar(count) =>
      for {
        inventory <- steamClient.getInventory(steamId, count)
        refreshId <- randomUUID().pure[IO]
        assets    <- maps.toAssets(refreshId, steamId, inventory)
        _         <- pgClient.insert(assets)
        response  <- Ok("success")
      } yield response
      
    // set state of asset to `trading` || `nottrading`. return error if state already === 
//    case PUT -> Root / "asset" :? UuidQPM(uuidValidated) +& StateToQPM(statetoValidated) =>
//      (uuidValidated, statetoValidated)
//        .mapN((uuid, stateto) => for {
//          asset <- pgClient.select(uuid)
//        } yield Ok("hi"))
//        .valueOr(errors       => BadRequest(errors.show))
//      match {
//        case (Invalid(a),  Invalid(b))     => BadRequest((a ::: b).show)
//        case (Invalid(a),  _)              => BadRequest(a.show)
//        case (_,           Invalid(b))     => BadRequest(b.show)
//        case (Valid(uuid), Valid(stateto)) => Ok(s"$uuid-----$stateto")
//      }
  
  }
}
