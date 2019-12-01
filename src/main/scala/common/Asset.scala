package common

import java.util.UUID

final case class Asset(
  id:               UUID,
  refresh_id:       UUID,
  steam_id:         String,
  appid:            Int,
  assetid:          String,
  classid:          String,
  instanceid:       String,
  tradable:         Boolean,
  market_hash_name: String,
  icon_url:         String,
  asset_type:       String,         // `type` in steam response
  exterior:         Option[String], // generated from `descriptions.tags`. not present on all
  rarity:           String,         // generated from `descriptions.tags`
  link_id:          Option[String], // generated from `descriptions.market_actions[index].link`. not present on all descriptions
  sticker_info:     String
)
