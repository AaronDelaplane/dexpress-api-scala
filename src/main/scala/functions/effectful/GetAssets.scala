package functions.effectful

import java.util.UUID.randomUUID

import cats.effect.{Clock, IO}
import cats.instances.long._
import cats.syntax.applicative._
import cats.syntax.option._
import clients.postgres.ClientPostgres
import clients.steam.ClientSteam
import codecs._
import functions.noneffectful.toassets.ToAssets.toAssets
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import types._

import scala.concurrent.duration.MILLISECONDS

class GetAssets(cS: ClientSteam, cP: ClientPostgres)(implicit C: Clock[IO]) extends Http4sDsl[IO] {
  
  def run(iS: IdSteam, c: Count): IO[Response[IO]] =
    for {
      time         <- C.monotonic(MILLISECONDS)
      maybeEvents  <- cP.selectEventsRefreshAssets(iS)
      maybeEventId  = maybeEvents.flatMap(toMaybeNonExpiredEventId(_, time, 10000))
      assets       <- maybeEventId.fold(refreshAssets(iS, c, time).flatMap(cP.selectAssets))(cP.selectAssets)
      response     <- Ok(assets)
    } yield response

  private def toMaybeNonExpiredEventId(xs: NEL[EventRefreshAssets], timeA: Long, timeB: Long): Option[IdRefresh] = {
    val head = xs.sortBy(_.time).reverse.head
    if (head.time - timeA < timeB) IdRefresh(head.refresh_id).some else None
  }

  private def refreshAssets(iS: IdSteam, c: Count, time: Long): IO[IdRefresh] =
    for {
      inventory <- cS.getInventory(iS, c.value)
      idRefresh <- IdRefresh(randomUUID()).pure[IO]
      assets    <- toAssets(idRefresh, iS, inventory)
      _         <- cP.insertMany(assets, idRefresh, iS, time)
    } yield idRefresh

}
