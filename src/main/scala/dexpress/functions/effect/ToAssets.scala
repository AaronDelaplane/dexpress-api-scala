package dexpress.functions.effect

import java.util.UUID.randomUUID

import cats.effect.{Clock, IO}
import cats.instances.long.catsKernelStdOrderForLong
import cats.syntax.applicative._
import dexpress.clients.postgres.ClientPostgres
import dexpress.clients.steam.ClientSteam
import dexpress.codecs._
import dexpress.functions.noneffect
import dexpress.functions.noneffect.{randomUUIDF, toHttpErrorResponse}
import dexpress.types._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

import scala.concurrent.duration.SECONDS

class ToAssets(cS: ClientSteam, cP: ClientPostgres)(implicit C: Clock[IO]) extends Http4sDsl[IO] {

  private val logger = Slf4jLogger.getLogger[IO]
  
  def runToResponse(sT: StateTrading, sO: SearchOffset, sL: SearchLimit): IO[Response[IO]] =
    cP
      .selectAssets(sT)
      .map(_.sortBy(_.id_asset))
      .map(_.slice(sO.value, sO.value + sL.value))
      .flatMap(Ok(_))
      .handleErrorWith(toHttpErrorResponse)
  
  def runToResponseFilterNot(sT: StateTrading, sO: SearchOffset, sL: SearchLimit, sFN: SearchFilterNot): IO[Response[IO]] =
    cP.selectAssetsFilterNot(sT, IdUser(sFN.value))
      .map(_.sortBy(_.id_asset))
      .map(_.slice(sO.value, sO.value + sL.value))
      .flatMap(Ok(_))
      .handleErrorWith(toHttpErrorResponse)
  
  def runToResponseFilter(sT: StateTrading, sO: SearchOffset, sL: SearchLimit, iU: IdUser): IO[Response[IO]] =
    run(sT, iU)
      .map(_.sortBy(_.id_asset))
      .map(_.slice(sO.value, sO.value + sL.value))
      .flatMap(Ok(_))
      .handleErrorWith(toHttpErrorResponse)
  
  def run(sT: StateTrading, iU: IdUser): IO[List[Asset]] =
    for {
      timeNow         <- C.realTime(SECONDS)
      maybeEvents     <- cP.selectMaybeEventsRefreshAssets(iU)
      maybeMostRecent  = maybeEvents.map(_.sortBy(_.time).reverse.head)
      idRefresh       <- maybeMostRecent match {
                           case None                              => refreshClean(iU, timeNow)
                           case Some(x) if (timeNow - x.time > 5) => refreshIterative(sT, IdRefresh(x.id_refresh), iU, timeNow)
                           case Some(x)                           => IdRefresh(x.id_refresh).pure[IO]
                         }
      assets          <- cP.selectAssetsFilter(sT, idRefresh)
    } yield assets

  private def refreshClean(iU: IdUser, timeNow: Long): IO[IdRefresh] =
    for {
      iUS       <- cP.select(iU).map(u => IdUserSteam(u.id_user_steam))
      inventory <- cS.getInventory(iUS)
      iR        <- randomUUIDF.map(IdRefresh)
      assets    <- noneffect.toassets.ToAssets.run(iR, iU, iUS, inventory)
      _         <- cP.insertMany(assets, iR, iU, timeNow)
    } yield iR
  
  private def refreshIterative(sT: StateTrading, iR_A: IdRefresh, iU: IdUser, timeNow: Long): IO[IdRefresh] =
    for {
      iUS       <- cP.select(iU).map(u => IdUserSteam(u.id_user_steam))
      inventory <- cS.getInventory(iUS)
      iR_B       = IdRefresh(randomUUID())
      assetsA   <- cP.selectAssetsFilter(sT, iR_A)
      assetsB   <- noneffect.toassets.ToAssets.runCombine(iR_B, iU, iUS, inventory, assetsA)
      _         <- cP.replace(iR_A, iR_B, assetsB, iU, timeNow)
      _         <- logger.info({
                     val idsA = assetsA.map(_.id_asset).toList
                     val idsB = assetsB.map(_.id_asset).toList
                     s"""
                       |refresh-iterative-summary
                       |  assets-count-previous:         ${assetsA.size}
                       |  assets-count-current:          ${assetsB.size}
                       |  assets-count-subtractions:     ${idsA.filterNot(idsB.contains).size}
                       |  assets-count-transfers:        ${idsA.intersect(idsB).size}
                       |  assets-count-additions:        ${idsB.filterNot(idsA.contains).size}
                       |  assets-trading-count-previous: ${assetsA.filter(_.is_trading).size}
                       |  assets-trading-count-current:  ${assetsB.filter(_.is_trading).size}
                       |""".stripMargin
                   })
    } yield iR_B
    
}
