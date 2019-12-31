package functions.effectful

import java.util.UUID.randomUUID

import cats.effect.{Clock, IO}
import cats.syntax.applicative._
import clients.postgres.ClientPostgres
import clients.steam.ClientSteam
import codecs._
import functions.noneffectful.toMaybeNonExpiredEventId
import functions.noneffectful.toassets.ToAssets.toAssets
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import types._

import scala.concurrent.duration.SECONDS

class ToAssets(cS: ClientSteam, cP: ClientPostgres)(implicit C: Clock[IO]) extends Http4sDsl[IO] {
  
  def run(iS: IdSteam, c: Count): IO[Response[IO]] =
    for {
      time         <- C.realTime(SECONDS)
      maybeEvents  <- cP.selectEventsRefreshAssets(iS)
      maybeEventId  = maybeEvents.flatMap(toMaybeNonExpiredEventId(_, time, 5))
      assets       <- maybeEventId.fold(refreshAssets(iS, c, time).flatMap(cP.selectAssets))(cP.selectAssets)
      response     <- Ok(assets)
    } yield response

  private def refreshAssets(iS: IdSteam, c: Count, time: Long): IO[IdRefresh] =
    for {
      inventory <- cS.getInventory(iS, c.value)
      idRefresh <- IdRefresh(randomUUID()).pure[IO]
      assets    <- toAssets(idRefresh, iS, inventory)
      _         <- cP.insertMany(assets, idRefresh, iS, time)
    } yield idRefresh

}
