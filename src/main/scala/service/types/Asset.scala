package service.types

import java.util.UUID
import cats.Monoid
import cats.instances.string._
import cats.instances.int._

import org.http4s.Uri

final case class Asset(
  id_asset:          UUID,
  id_refresh:        UUID,
  trading:           Boolean,
  steam_id:          String,
  floatvalue:        Option[Double],
  classid:           String,
  instanceid:        String,
  appid:             Int,
  assetid:           String,
  amount:            String,  
  market_hash_name:  String,
  icon_url:          String,
  tradable:          Int,               // todo possibly remove. if 0, no write to datastore should occur
  `type`:            String,            // `type` in steam response
  link_id:           Option[String],
  sticker_urls:      Option[List[Uri]], // todo make nonemptylist
  
  tag_exterior_category:                Option[String],
  tag_exterior_internal_name:           Option[String],
  tag_exterior_localized_category_name: Option[String],
  tag_exterior_localized_tag_name:      Option[String],
  
  tag_rarity_category:                  Option[String],
  tag_rarity_internal_name:             Option[String],
  tag_rarity_localized_category_name:   Option[String],
  tag_rarity_localized_tag_name:        Option[String],
  tag_rarity_color:                     Option[String],
     
  tag_type_category:                    Option[String],
  tag_type_internal_name:               Option[String],
  tag_type_localized_category_name:     Option[String],
  tag_type_localized_tag_name:          Option[String],
  
  tag_weapon_category:                  Option[String],
  tag_weapon_internal_name:             Option[String],
  tag_weapon_localized_category_name:   Option[String],
  tag_weapon_localized_tag_name:        Option[String],
  
  tag_quality_category:                 Option[String],
  tag_quality_internal_name:            Option[String],
  tag_quality_localized_category_name:  Option[String],
  tag_quality_localized_tag_name:       Option[String],
  tag_quality_color:                    Option[String],
)

object Asset {
  
  val keys = Set(
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

  def apply(iS: IdSteam, iR: IdRefresh)(t: (SDV, SAV)): Asset = {
    val d = t._1
    val a = t._2
    Asset(
      id_asset         = UUID.randomUUID,
      id_refresh       = iR.value,
      trading          = false,
      steam_id         = iS.value,
      floatvalue       = None,
      classid          = d.classid,
      instanceid       = d.instanceid,
      appid            = d.appid,
      assetid          = a.assetid,
      amount           = a.amount,
      market_hash_name = d.market_hash_name,
      icon_url         = d.icon_url,
      tradable         = d.tradable,
      `type`           = d.`type`,
      link_id          = d.link_id,
      sticker_urls     = d.sticker_urls,
      tag_exterior_category                = d.tagExterior map(_.category),
      tag_exterior_internal_name           = d.tagExterior map(_.internal_name),
      tag_exterior_localized_category_name = d.tagExterior map(_.localized_category_name),
      tag_exterior_localized_tag_name      = d.tagExterior map(_.localized_tag_name),
      tag_rarity_category                  = d.tagRarity   map(_.category),
      tag_rarity_internal_name             = d.tagRarity   map(_.internal_name),
      tag_rarity_localized_category_name   = d.tagRarity   map(_.localized_category_name),
      tag_rarity_localized_tag_name        = d.tagRarity   map(_.localized_tag_name),
      tag_rarity_color                     = d.tagRarity   map(_.color),
      tag_type_category                    = d.tagType     map(_.category),
      tag_type_internal_name               = d.tagType     map(_.internal_name),
      tag_type_localized_category_name     = d.tagType     map(_.localized_category_name),
      tag_type_localized_tag_name          = d.tagType     map(_.localized_tag_name),
      tag_weapon_category                  = d.tagWeapon   map(_.category),
      tag_weapon_internal_name             = d.tagWeapon   map(_.internal_name),
      tag_weapon_localized_category_name   = d.tagWeapon   map(_.localized_category_name),
      tag_weapon_localized_tag_name        = d.tagWeapon   map(_.localized_tag_name),
      tag_quality_category                 = d.tagQuality  map(_.category),
      tag_quality_internal_name            = d.tagQuality  map(_.internal_name),
      tag_quality_localized_category_name  = d.tagQuality  map(_.localized_category_name),
      tag_quality_localized_tag_name       = d.tagQuality  map(_.localized_tag_name),
      tag_quality_color                    = d.tagQuality  flatMap(_.color),
    )
  }
  
}
