package clients.steam

import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.{ConcurrentEffect, IO, Resource}
import cats.implicits._
import clients.data.Asset
import clients.steam.data._
import common.ErrorOr
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Uri}

import scala.concurrent.ExecutionContext.global

// @formatter:off
class SteamClient(config: SteamClientConfig, httpClient: Client[IO]) extends Http4sDsl[IO] {
  
  val log = Slf4jLogger.getLogger[IO]
  
  def isMatch(tagName: String)(tag: SteamTag): Boolean =
    tag.category
      .map(_.toLowerCase === tagName)
      .getOrElse(false)
  
  def toLocalizedTagName(st: SteamTag): ErrorOr[String] =
    st.localized_tag_name
      .fold("localized_tag_name-not-defined".asLeft[String])(_.asRight)
  
  def toTagString(tags: NonEmptyList[SteamTag], tagName: String): ErrorOr[String] =
    tags
      .find(isMatch(tagName))
      .fold(s"tag-$tagName-not-found".asLeft[SteamTag])(_.asRight)
      .flatMap(toLocalizedTagName)
  
  def toMaybeTagString(tags: NonEmptyList[SteamTag], tagName: String): ErrorOr[Option[String]] =
    tags
      .find(isMatch(tagName))
      .fold[ErrorOr[Option[String]]](None.asRight)(toLocalizedTagName(_).map(Some.apply))
  
  def buildAsset(refreshId: UUID, steamId: String, a: SteamAsset)(d: SteamDescription): ErrorOr[Asset] =
    for {
      id               <- UUID.randomUUID().asRight[String]
      appid            <- a.appid.fold("appid-not-defined".asLeft[Int])(_.asRight)
      assetid          <- a.assetid.fold("assetid-not-defined".asLeft[String])(_.asRight)
      classid          <- a.classid.fold("classid-not-defined".asLeft[String])(_.asRight)
      instanceid       <- a.instanceid.fold("instanceid-not-defined".asLeft[String])(_.asRight)
      tradable         <- d.tradable.fold("tradable-not-defined".asLeft[Int])(_.asRight)
      market_hash_name <- d.market_hash_name.fold("market_hash_name-not-defined".asLeft[String])(_.asRight)
      icon_url         <- d.icon_url.fold("icon_url-not-defined".asRight[String])(_.asRight)
      asset_type       <- d.`type`.fold("type-not-defined".asLeft[String])(_.asRight)
      _tags            <- d.tags.fold("tags-not-defined".asLeft[List[SteamTag]])(_.asRight)
      _nonEmptyTags    <- _tags.toNel.fold("tags-is-empty".asLeft[NonEmptyList[SteamTag]])(_.asRight)
      exterior         <- toMaybeTagString(_nonEmptyTags, "exterior")
      rarity           <- toTagString(_nonEmptyTags, "rarity")
      item_data        <- "item_data".asRight[String] // d.market_actions // transform to `item_data`
      sticker_info     <- "sticker-info".asRight[String]
    } yield
      Asset(
        id               = id,
        refresh_id       = refreshId,
        steam_id         = steamId,
        appid            = appid,
        assetid          = assetid,
        classid          = classid,
        instanceid       = instanceid,
        tradable         = tradable,
        market_hash_name = market_hash_name,
        icon_url         = icon_url,
        asset_type       = asset_type,
        exterior         = exterior,
        rarity           = rarity,
        item_data        = item_data,
        sticker_info     = sticker_info
      )
     
  private def buildAssets(refreshId: UUID, steamId: String, xs: (NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])): ErrorOr[NonEmptyList[Asset]] = {
    type R = NonEmptyList[Asset]
    val (errors, assets) =
      xs._1.toList.map(a =>  
        xs._2
          .find(d => d.classid === a.classid && d.instanceid === a.instanceid)
          .fold("no-matching-description-for-asset".asLeft[Asset])(buildAsset(refreshId, steamId, a))
        ).separate 
    (NonEmptyList.fromList(errors), NonEmptyList.fromList(assets)) match {
      case (None, None)         => "no-errors-or-assets-generated".asLeft[R]
      case (Some(_), Some(_))   => "errors-&-assets-generated".asLeft[R]  
      case (Some(_), None)      => "only-errors-generated".asLeft[R]
      case (None, Some(assets)) => assets.asRight[String]
    }
  }
  
  def getSteamInventory(steamId: String, count: Int): IO[SteamInventory] = {
    httpClient.expect[SteamInventory](
      Request[IO]()
        .withMethod(GET)
        .withUri(Uri.unsafeFromString(s"${config.steamUri}/inventory/$steamId/730/2?l=english&count=$count"))
      )
}
  
  def toAssetsAndDescriptions(si: SteamInventory): ErrorOr[(NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])] = {
    type R = (NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])
    (si.assets, si.descriptions) match {
      case (None, None) => "response-missing-assets-&-descriptions".asLeft[R]
      case (None, _)    => "response-missing-assets".asLeft[R]
      case (_, None)    => "response-missing-descriptions".asLeft[R]
      case (Some(assets), Some(descriptions)) => {
        (NonEmptyList.fromList(assets), NonEmptyList.fromList(descriptions)) match {
          case (None, None)         => "assets-&-descriptions-empty".asLeft[R]
          case (None, Some(_))      => "assets-empty-descriptions-non-empty".asLeft[R]
          case (Some(_), None)      => "descriptions-empty-assets-non-empty".asLeft[R]
          case (Some(as), Some(ds)) => ((as, ds)).asRight[String]  
        }
      }
    }
  }
  
  def sourceAssets(refreshId: UUID, steamId: String, count: Int): IO[NonEmptyList[Asset]] =
    for {
      _              <- log.info("attempting request")
      steamInventory <- getSteamInventory(steamId, count)
      _              <- log.info(s"""
                          |steam-inventory-fetch-results:
                          |  assets-count:       ${steamInventory.assets.map(_.size)}
                          |  descriptions-count: ${steamInventory.descriptions.map(_.size)}
                          |  unique-assetids:    ${steamInventory.assets.map(_.map(_.assetid).distinct.size).get}
                          |  unique-classids:    ${steamInventory.assets.map(_.map(_.classid).distinct.size).get}
                          |  unique-instanceids: ${steamInventory.assets.map(_.map(_.instanceid).distinct.size).get}
                          |""".stripMargin)
      xs             <- toAssetsAndDescriptions(steamInventory).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      assets         <- buildAssets(refreshId, steamId, xs).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      _              <- log.info(s"""
                          |generated-assets-summary
                          |  assets-count: ${assets.size}
                          |""".stripMargin)
    } yield assets
  
}

object SteamClient {
  def resource(config: SteamClientConfig)(implicit CE: ConcurrentEffect[IO]): Resource[IO, SteamClient] =
    BlazeClientBuilder[IO](global).resource.map(new SteamClient(config, _))
}
//@formatter:on
