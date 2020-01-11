package dexpress.clients.postgres

import cats.effect.IO
import cats.syntax.list._
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import dexpress.codecs._
import dexpress.types._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import dexpress.functions.effect._

class ClientPostgres(xa: Transactor[IO]) extends Http4sDsl[IO] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler

  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
  def insertMany(xs: NEL[Asset], iR: IdRefresh, iS: IdSteam, time: Long): IO[Unit] = (
    for {
      _ <- Statements.insertAssets.updateMany(xs)
      _ <- Statements.insertEventAssetsRefresh(iR, iS, time).run
    } yield ()
  )
    .transact(xa)
    .handleErrorWith(handleErrorT("attempt to insert assets failed"))
    

  def replace(iRA: IdRefresh, iRB: IdRefresh, xsB: NEL[Asset], iS: IdSteam, time: Long): IO[Unit] = (
    for {
      _ <- Statements.delete(iRA).run
      _ <- Statements.insertAssets.updateMany(xsB)
      _ <- Statements.insertEventAssetsRefresh(iRB, iS, time).run
    } yield ()
  )
    .transact(xa)
    .handleErrorWith(handleErrorT("attempt to replace assets failed"))
    
  def selectAsset(iA: IdAsset): IO[Asset] =
    Statements.selectAsset(iA).unique
      .transact(xa)
      .handleErrorWith(handleErrorT("attempt to select asset failed"))

  def selectAssets(iR: IdRefresh): IO[NEL[Asset]] =
    Statements.selectAssets(iR)
      .to[List]
      .transact(xa)
      .handleErrorWith(handleErrorT("attempt to select assets failed"))
      .flatMap[NEL[Asset]](
        _.toNel.fold[IO[NEL[Asset]]](IO.raiseError(ServiceError(s"no assets found for refresh id: $iR")))(IO.pure(_))
      )
  
  def selectEventsRefreshAssets(iS: IdSteam): IO[Option[NEL[EventAssetsRefresh]]] =
    Statements.selectEventsAssetsRefresh(iS)
      .to[List]
      .transact(xa)
      .map(_.toNel)

  def updateAsset(iA: IdAsset, sT: StateTrading): IO[Asset] =
    Statements.updateAsset(iA, sT)
      .withUniqueGeneratedKeys[Asset](
        "id_asset",
        "id_refresh",
        "trading",
        "steam_id",
        "floatvalue",
        "classid",
        "instanceid",
        "appid",
        "assetid",
        "amount",
        "market_hash_name",
        "icon_url",
        "tradable",
        "type",
        "link_id",
        "sticker_urls",
        "tag_exterior_category",
        "tag_exterior_internal_name",
        "tag_exterior_localized_category_name",
        "tag_exterior_localized_tag_name",
        "tag_rarity_category",
        "tag_rarity_internal_name",
        "tag_rarity_localized_category_name",
        "tag_rarity_localized_tag_name",
        "tag_rarity_color",
        "tag_type_category",
        "tag_type_internal_name",
        "tag_type_localized_category_name",
        "tag_type_localized_tag_name",
        "tag_weapon_category",
        "tag_weapon_internal_name",
        "tag_weapon_localized_category_name",
        "tag_weapon_localized_tag_name",
        "tag_quality_category",
        "tag_quality_internal_name",
        "tag_quality_localized_category_name",
        "tag_quality_localized_tag_name",
        "tag_quality_color"
      )
      .transact(xa)
      .handleErrorWith(handleErrorT("attempt to update asset failed"))
  
  def updateAsset(iA: IdAsset, sT: StateTrading, fV: FloatValue): IO[Asset] =
    Statements.updateAsset(iA, sT, fV)
      .withUniqueGeneratedKeys[Asset](
        "id_asset",
        "id_refresh",
        "trading",
        "steam_id",
        "floatvalue",
        "classid",
        "instanceid",
        "appid",
        "assetid",
        "amount",
        "market_hash_name",
        "icon_url",
        "tradable",
        "type",
        "link_id",
        "sticker_urls",
        "tag_exterior_category",
        "tag_exterior_internal_name",
        "tag_exterior_localized_category_name",
        "tag_exterior_localized_tag_name",
        "tag_rarity_category",
        "tag_rarity_internal_name",
        "tag_rarity_localized_category_name",
        "tag_rarity_localized_tag_name",
        "tag_rarity_color",
        "tag_type_category",
        "tag_type_internal_name",
        "tag_type_localized_category_name",
        "tag_type_localized_tag_name",
        "tag_weapon_category",
        "tag_weapon_internal_name",
        "tag_weapon_localized_category_name",
        "tag_weapon_localized_tag_name",
        "tag_quality_category",
        "tag_quality_internal_name",
        "tag_quality_localized_category_name",
        "tag_quality_localized_tag_name",
        "tag_quality_color"
      )
      .transact(xa)
      .handleErrorWith(handleErrorT("attempt to update asset failed"))
  
}
