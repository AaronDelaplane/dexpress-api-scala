package dexpress.types

import java.util.UUID

import cats.Eval
import org.http4s.Uri

final case class Asset(
  id_asset:          UUID,
  id_user:           UUID,
  id_refresh:        UUID,
  is_trading:        Boolean,
  id_user_steam:     String,
  float_value:       Option[Double],
  id_class:          String,
  id_instance:       String,
  id_app:            Int,
  id_asset_steam:    String,
  amount:            String,  
  market_hash_name:  String,
  icon_url:          String,
//tradable:          Int,               // todo possibly remove. if 0, no write to datastore should occur
  type_asset:        String,            // `type` in steam response
  id_link:           Option[String],
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
//  "tradable",
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

  def apply(iU: IdUser, iUS: IdUserSteam, iR: IdRefresh, uuid: UUID = Eval.always(UUID.randomUUID).value)(t: (SDV, SAV)): Asset = {
    val d = t._1
    val a = t._2
    Asset(
      id_asset         = uuid,
      id_user          = iU.value,
      id_refresh       = iR.value,
      is_trading       = false,
      id_user_steam    = iUS.value,
      float_value      = None,
      id_class         = d.classid,
      id_instance      = d.instanceid,
      id_app           = d.appid,
      id_asset_steam   = a.id_asset_steam,
      amount           = a.amount,
      market_hash_name = d.market_hash_name,
      icon_url         = d.icon_url,
//    tradable         = d.tradable,
      type_asset       = d.`type`,
      id_link          = d.link_id,
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
