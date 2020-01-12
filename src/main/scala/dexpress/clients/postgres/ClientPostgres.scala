package dexpress.clients.postgres

import cats.Applicative
import cats.effect.{IO, Timer}
import cats.syntax.applicative._
import cats.syntax.apply._
import cats.syntax.either._
import cats.syntax.list._
import dexpress.enums.ResourceName.Postgres
import dexpress.types._
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import mouse.boolean._
import org.http4s.dsl.Http4sDsl
import retry._
import doobie.postgres.implicits._
import dexpress.codecs._

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration._
/*
required imports that IntelliJ's optimize imports command will remove:
import doobie.postgres.implicits._
import dexpress.codecs._
 */


class ClientPostgres(xa: Transactor[IO]) extends Http4sDsl[IO] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler

  implicit def logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]
  
  implicit val timer: Timer[IO] = IO.timer(global)
  
  /*
  assets table ---------------------------------------------------------------------------------------------------------
   */
  // todo you perhaps can just use 'unique', though that may require a non-empty result
  def selectExistingUniqueAsset(iA: IdAsset): IO[Either[ResourceError, Asset]] =
    Statements.selectAsset(iA).to[List]
      .transact[IO](xa)
      .map(xs => xs match {
        case h :: Nil => h.asRight[ResourceError]
        case Nil      => ResourceNotFoundError(Postgres, s"asset with id ($iA)").asLeft[Asset]
        case _        => ResourceDuplicationError(Postgres, s"assets with id ($iA)").asLeft[Asset]
      })
      .handleErrorWith(
        ResourceTransactionError(Postgres, s"select asset with id ($iA)", _).asLeft[Asset].pure[IO]
      )

  def updateAsset(iA: IdAsset, sT: StateTrading): IO[Asset] =
    Statements.updateAsset(iA, sT)
      .withUniqueGeneratedKeys[Asset](
        "id_asset",
        "id_user",
        "id_refresh",
        "is_trading",
        "id_user_steam",
        "float_value",
        "id_class",
        "id_instance",
        "id_app",
        "id_asset_steam",
        "amount",
        "market_hash_name",
        "icon_url",
        "type_asset",
        "id_link",
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
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, "update asset", t))
      )

  def updateAsset(iA: IdAsset, sT: StateTrading, fV: FloatValue): IO[Asset] =
    Statements.updateAsset(iA, sT, fV)
      .withUniqueGeneratedKeys[Asset](
        "id_asset",
        "id_user",
        "id_refresh",
        "is_trading",
        "id_user_steam",
        "float_value",
        "id_class",
        "id_instance",
        "id_app",
        "id_asset_steam",
        "amount",
        "market_hash_name",
        "icon_url",
        "type_asset",
        "id_link",
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
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, "update asset", t))
      )
  
  /*
  assets ---------------------------------------------------------------------------------------------------------------
   */
  def selectAssets(sT: StateTrading): IO[List[Asset]] =
    Statements.selectAssets(sT)
      .to[List]
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, s"select assets with is_trading (${sT.value}", t))
      )
    
  def selectAssetsFilter(sT: StateTrading, iR: IdRefresh): IO[List[Asset]] =
    Statements.selectAssetsFilter(sT, iR)
      .to[List]
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(
          ResourceTransactionError(Postgres, s"select assets with is_trading (${sT.value} and id_refresh (${iR.value})", t)
        )
      )
  
  def selectAssetsFilterNot(sT: StateTrading, iU: IdUser): IO[List[Asset]] =
    Statements.selectAssetsFilterNot(sT, iU)
      .to[List]
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(
          ResourceTransactionError(Postgres, s"select assets with is_trading (${sT.value}) and id_user (${iU.value})", t)
        )
      )
  
  /*
  events refresh assets ------------------------------------------------------------------------------------------------
   */
  def selectMaybeEventsRefreshAssets(iU: IdUser): IO[Option[NEL[EventAssetsRefresh]]] =
    Statements.selectEventsAssetsRefresh(iU)
      .to[List]
      .transact(xa)
      .map(_.toNel)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, "select maybe events refresh assets", t))
      )

  /*
  users ----------------------------------------------------------------------------------------------------------------
   */
  def exists(iUS: IdUserSteam): IO[Boolean] =
    Statements.exists(iUS)
      .unique
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, s"check if user with id_user_steam (${iUS.value}) exists", t))
      )
  
  def insert(u: User): IO[User] =
    Statements.insertUser(u)
      .withUniqueGeneratedKeys[User]("id_user", "id_user_steam", "name_first")
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, "insert user", t))
      )
  
  def select(iU: IdUser): IO[User] =
    Statements.selectUser(iU)
      .unique
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, s"select user with id_user (${iU.value}", t))
      )
  
  def select(iUS: IdUserSteam): IO[User] =
    Statements.selectUser(iUS)
      .unique
      .transact(xa)
      .handleErrorWith(
        t => IO.raiseError(ResourceTransactionError(Postgres, s"select user with id_user_steam (${iUS.value}", t))
      )

  // compositions ------------------------------------------------------------------------------------------------------
  def insertMany(xs: NEL[Asset], iR: IdRefresh, iU: IdUser, time: Long): IO[Unit] = (
    for {
      _ <- Statements.insertAssets.updateMany(xs)
      _ <- Statements.insertEventAssetsRefresh(iR, iU, time).run
    } yield ()
    )
    .transact(xa)
    .handleErrorWith(
      t => IO.raiseError(ResourceTransactionError(Postgres, "insert assets", t))
    )
  
  def replace(iRA: IdRefresh, iRB: IdRefresh, xsB: NEL[Asset], iU: IdUser, time: Long): IO[Unit] = 
    (
      for {
        _ <- Statements.delete(iRA).run
        _ <- Statements.insertAssets.updateMany(xsB)
        _ <- Statements.insertEventAssetsRefresh(iRB, iU, time).run
      } yield ()
    )
    .transact(xa)
    .handleErrorWith(
      t => IO.raiseError(ResourceTransactionError(Postgres, "replace assets", t))
    )
  
  // utility functions -------------------------------------------------------------------------------------------------
  def verifyConnection: IO[Unit] =
    retryingOnAllErrors(
      policy  = RetryPolicies.constantDelay(500.milliseconds)(Applicative[IO]),
      onError = (_: Throwable, rD: RetryDetails) => rD.givingUp.fold(
        logger.info("attempt to connect to postgres failed. retrying...") *> IO.raiseError(new RuntimeException),
        IO.unit
      )
    )(1.pure[ConnectionIO].transact(xa).map(_ => ()))
  
}
