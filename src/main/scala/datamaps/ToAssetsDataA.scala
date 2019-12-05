package datamaps

import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import datatypes.{AssetDataA, MaybeAssetDataA, SteamAsset, SteamDescription, SteamInventory, SteamTag, _}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToAssetsDataA {
  
  val log = Slf4jLogger.getLogger[IO]

  def run(refreshId: UUID, steamId: String, steamInventory: SteamInventory): IO[NonEmptyList[AssetDataA]] =
    for {
      xs          <- toAssetsAndDescriptions(steamInventory).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      _           <- log.info({
                       val as = xs._1.toList
                       val ds = xs._2.toList
                       s"""
                         |unjoined-descriptions-summary
                         |  descriptions-count:  ${ds.size}
                         |  description-orphans: ${ds.filterNot(d => as.exists(a => isPair(a)(d))).size}   
                         |  has-market-actions:  ${ds.filter(_.market_actions.isDefined).size}
                         |  has-tags:            ${ds.filter(_.tags.nonEmpty).size}
                         |  has-exterior-tag:    ${ds.flatMap(_.tags).filter(_.exists(isTag("exterior"))).size}   
                         |""".stripMargin
                     })
      maybeAssets <- buildMaybeAssets(refreshId, steamId, xs).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      assets      <- maybeAssets.toList.flatMap(AssetDataA.fromMaybe).toNel
                       .fold[IO[NonEmptyList[AssetDataA]]](IO.raiseError(new Exception("zero-assets-map-to-tradable-asset")))(_.pure[IO])                      
      _           <- log.info(s"""
                       |generated-assets-summary
                       |  maybe-tradable-assets-count: ${maybeAssets.size}
                       |  tradable-assets-count:       ${assets.size}
                       |""".stripMargin)
    } yield assets

  private def toAssetsAndDescriptions(si: SteamInventory): ErrorOr[(NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])] = {
    type R = (NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])
    (si.assets, si.descriptions) match {
      case (None, None) => "missing-assets-&-descriptions".asLeft[R]
      case (None, _)    => "missing-assets".asLeft[R]
      case (_, None)    => "missing-descriptions".asLeft[R]
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

  private def buildMaybeAssets(refreshId: UUID, steamId: String, xs: (NonEmptyList[SteamAsset], NonEmptyList[SteamDescription])): ErrorOr[NonEmptyList[MaybeAssetDataA]] = {
    type R = NonEmptyList[MaybeAssetDataA]
    val (errors, assets) =
      xs._1.toList.map(a =>
        xs._2
          .find(isPair(a))
          .fold("no-matching-description-for-asset".asLeft[MaybeAssetDataA])(buildMaybeAsset(refreshId, steamId, a))
      ).separate
    (NonEmptyList.fromList[String](errors), NonEmptyList.fromList[MaybeAssetDataA](assets)) match {
      case (None, None)            => "no-errors-or-assets-generated".asLeft[NonEmptyList[MaybeAssetDataA]]
      case (Some(errors), Some(_)) => s"errors-&-assets-generated: $errors".asLeft[NonEmptyList[MaybeAssetDataA]]
      case (Some(errors), None)    => s"only-errors-generated: $errors".asLeft[NonEmptyList[MaybeAssetDataA]]
      case (None, Some(assets))    => assets.asRight[String]
    }
  }
  
  private def isPair(a: SteamAsset)(d: SteamDescription): Boolean =
    (d.classid === a.classid) && (d.instanceid === a.instanceid)

  private def buildMaybeAsset(refreshId: UUID, steamId: String, a: SteamAsset)(d: SteamDescription): ErrorOr[MaybeAssetDataA] =
    for {
      id               <- UUID.randomUUID().asRight[String]
      appid            <- a.appid.fold("appid-not-defined".asLeft[Int])(_.asRight)
      assetid          <- a.assetid.fold("assetid-not-defined".asLeft[String])(_.asRight)
      _log_id           = s"appid=${appid}_assetid=$assetid"
      classid          <- a.classid.fold(s"classid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      instanceid       <- a.instanceid.fold(s"instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      tradable         <- d.tradable.fold(s"tradable-not-defined--${_log_id}".asLeft[Boolean])(
                            toMaybeBoolean(_).fold(s"tradable-not-0-or-1--${_log_id}".asLeft[Boolean])(_.asRight[String])
                          )
      market_hash_name <- d.market_hash_name.fold(s"market_hash_name-not-defined--${_log_id}".asLeft[String])(_.asRight)
      icon_url         <- d.icon_url.fold(s"icon_url-not-defined--${_log_id}".asRight[String])(_.asRight)
      asset_type       <- d.`type`.fold(s"type-not-defined--${_log_id}".asLeft[String])(_.asRight)
      _tags            <- d.tags.fold(s"tags-not-defined--${_log_id}".asLeft[List[SteamTag]])(_.asRight)
      _ne_tags         <- _tags.toNel.fold(s"tags-empty--${_log_id}".asLeft[NonEmptyList[SteamTag]])(_.asRight)
      maybe_exterior   <- toMaybeTagString(_ne_tags, "exterior")
      rarity           <- toTagString(_ne_tags, "rarity")
      maybe_link_id     = d.market_actions.flatMap(_.headOption).flatMap(_.link).flatMap(toMaybeLinkId)
      sticker_info     <- "sticker-info".asRight[String]
    } yield
      MaybeAssetDataA(
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
        exterior         = maybe_exterior,
        rarity           = rarity,
        link_id          = maybe_link_id,
        sticker_info     = sticker_info
      )
  
  private def toMaybeBoolean(n: Int): Option[Boolean] =
    n match {
      case 0 => Some(false)
      case 1 => Some(true)
      case _ => None
    }    
  
  private def toMaybeLinkId(s: String): Option[String] =
    s.split("%D").reverse.headOption 
  
  private def toTagString(tags: NonEmptyList[SteamTag], tagName: String): ErrorOr[String] =
    tags
      .find(isTag(tagName))
      .fold(s"tag-$tagName-not-found".asLeft[SteamTag])(_.asRight)
      .flatMap(toLocalizedTagName)

  private def toMaybeTagString(tags: NonEmptyList[SteamTag], tagName: String): ErrorOr[Option[String]] =
    tags
      .find(isTag(tagName))
      .fold[ErrorOr[Option[String]]](None.asRight)(toLocalizedTagName(_).map(Some.apply))

  private def toLocalizedTagName(st: SteamTag): ErrorOr[String] =
    st.localized_tag_name
      .fold("localized_tag_name-not-defined".asLeft[String])(_.asRight)

  private def isTag(tagName: String)(tag: SteamTag): Boolean =
    tag.category
      .map(_.toLowerCase === tagName)
      .getOrElse(false)
}
