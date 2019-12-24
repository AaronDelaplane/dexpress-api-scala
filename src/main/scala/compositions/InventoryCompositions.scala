package compositions

import java.util.UUID
import java.util.UUID.randomUUID

import cats.effect.{Clock, IO}
//import cats.implicits._
import cats.instances.long._
import cats.syntax.applicative._
import clients.postgres.ClientPostgres
import clients.steam.ClientSteam
import datamaps.toassets.ToAssets.toAssets
import datatypes._
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import codecs._

import scala.concurrent.duration.MILLISECONDS

class InventoryCompositions(clientSteam: ClientSteam, clientPg: ClientPostgres)(implicit C: Clock[IO]) extends Http4sDsl[IO] {

  def getInventory(steamId: String, count: Count): IO[Response[IO]] =
    for {
      time         <- C.monotonic(MILLISECONDS)
      maybeEvents  <- clientPg.selectEventsRefreshAssets(steamId)
      maybeEventId  = maybeEvents.flatMap(toMaybeNonExpiredEventId(_, time))
      inventory    <- maybeEventId.fold(refreshInventory(steamId, count, time).flatMap(clientPg.selectAssets))(clientPg.selectAssets)
      response     <- Ok(inventory)
    } yield response

  private def toMaybeNonExpiredEventId(nel: NEL[EventRefreshAssets], time: Long): Option[UUID] = {
    val head = nel.sortBy(_.time).reverse.head
    if (head.time - time < 10000) Some(head.refresh_id) else None
  }

  private def refreshInventory(steamId: String, count: Count, time: Long): IO[UUID] =
    for {
      inventory <- clientSteam.getInventory(steamId, count.value)
      refreshId <- randomUUID().pure[IO]
      assets    <- toAssets(refreshId, steamId, inventory)
      _         <- clientPg.insertMany(assets, refreshId, steamId, time)
    } yield refreshId

}
