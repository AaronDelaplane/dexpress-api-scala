package compositions

import java.util.UUID.randomUUID

import cats.effect._
import cats.implicits._
import clients.postgres.ClientPostgres
import clients.steam.ClientSteam
import datamaps.toassets.ToAssets.toAssets
import datatypes._
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

import scala.concurrent.duration.MILLISECONDS

object InventoryRefresh extends Http4sDsl[IO] {

  def refreshInventory(clientSteam: ClientSteam, clientPg: ClientPostgres, steamId: String, count: Count)(implicit C: Clock[IO]): IO[Response[IO]] =
    for {
      time        <- C.monotonic(MILLISECONDS)
      inventory   <- clientSteam.getInventory(steamId, count.value)
      refreshId   <- randomUUID().pure[IO]
      assetsDataA <- toAssets(refreshId, steamId, inventory)
      _           <- clientPg.insertMany(assetsDataA, refreshId, time)
      response    <- NoContent()
    } yield response
}
