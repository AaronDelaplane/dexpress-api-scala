package types

import java.util.UUID

import org.http4s.Uri

final case class Asset(
  dexpress_asset_id: UUID,
  refresh_id:        UUID,
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

  def apply(steamId: String, refreshId: UUID)(t: (SDV, SAV)): Asset = {
    val d = t._1
    val a = t._2
    Asset(
      dexpress_asset_id = UUID.randomUUID,
      refresh_id        = refreshId,
      trading           = false,
      steam_id          = steamId,
      floatvalue        = None,
      classid           = d.classid,
      instanceid        = d.instanceid,
      appid             = d.appid,
      assetid           = a.assetid,
      amount            = a.amount,
      market_hash_name  = d.market_hash_name,
      icon_url          = d.icon_url,
      tradable          = d.tradable,
      `type`            = d.`type`,
      link_id           = d.link_id,
      sticker_urls      = d.sticker_urls,
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