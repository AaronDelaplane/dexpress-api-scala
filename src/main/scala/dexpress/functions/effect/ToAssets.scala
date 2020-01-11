package dexpress.functions.effect

import java.util.UUID.randomUUID

import cats.effect.{Clock, IO}
import cats.instances.long.catsKernelStdOrderForLong
import cats.syntax.applicative._
import dexpress.clients.postgres.ClientPostgres
import dexpress.clients.steam.ClientSteam
import dexpress.codecs._
import dexpress.functions.noneffect
import dexpress.types._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

import scala.concurrent.duration.SECONDS

class ToAssets(cS: ClientSteam, cP: ClientPostgres)(implicit C: Clock[IO]) extends Http4sDsl[IO] {

  private val logger = Slf4jLogger.getLogger[IO]
  
  def runToResponse(iS: IdSteam): IO[Response[IO]] =
    run(iS)
      .flatMap(Ok(_))
      .handleErrorWith {
        case sE: ServiceError => InternalServerError(sE.message)
        case t:  Throwable    => InternalServerError(t.getMessage)
      }
  
  def run(iS: IdSteam): IO[NEL[Asset]] =
    for {
      timeNow         <- C.realTime(SECONDS)
      maybeEvents     <- cP.selectEventsRefreshAssets(iS)
      maybeMostRecent  = maybeEvents.map(_.sortBy(_.time).reverse.head)
      idRefresh       <- maybeMostRecent match {
                           case None                              => refreshClean(iS, timeNow)
                           case Some(x) if (timeNow - x.time > 5) => refreshIterative(IdRefresh(x.id_refresh), iS, timeNow)
                           case Some(x)                           => IdRefresh(x.id_refresh).pure[IO]
                         }
      assets          <- cP.selectAssets(idRefresh)
    } yield assets

  private def refreshClean(iS: IdSteam, timeNow: Long): IO[IdRefresh] =
    for {
      inventory <- cS.getInventory(iS)
      iR         = IdRefresh(randomUUID())
      assets    <- noneffect.toassets.ToAssets.run(iR, iS, inventory)
      _         <- cP.insertMany(assets, iR, iS, timeNow)
    } yield iR
  
  private def refreshIterative(iRA: IdRefresh, iS: IdSteam, timeNow: Long): IO[IdRefresh] =
    for {
      inventory <- cS.getInventory(iS)
      iRB        = IdRefresh(randomUUID())
      assetsA   <- cP.selectAssets(iRA)
      assetsB   <- noneffect.toassets.ToAssets.run(iRB, iS, inventory, assetsA)
      _         <- cP.replace(iRA, iRB, assetsB, iS, timeNow)
      _         <- logger.info({
                     val idsA = assetsA.map(_.assetid).toList
                     val idsB = assetsB.map(_.assetid).toList
                     s"""
                       |refresh-iterative-summary
                       |  assets-count-previous:         ${assetsA.size}
                       |  assets-count-current:          ${assetsB.size}
                       |  assets-count-subtractions:     ${idsA.filterNot(idsB.contains).size}
                       |  assets-count-transfers:        ${idsA.intersect(idsB).size}
                       |  assets-count-additions:        ${idsB.filterNot(idsA.contains).size}
                       |  assets-trading-count-previous: ${assetsA.filter(_.trading).size}
                       |  assets-trading-count-current:  ${assetsB.filter(_.trading).size}
                       |""".stripMargin
                   })
    } yield iRB
    
}