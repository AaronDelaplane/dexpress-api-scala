package clients.steam.data

// @formatter:off
final case class Asset(
  appid:            Int,
  assetid:          String,
  classid:          String,
  instanceid:       String,
  tradable:         Int,
  market_hash_name: String,
  icon_url:         String,
  asset_type:       String,
  exterior:         Option[String], // generated from `descriptions.tags`. not present on all
  rarity:           String,         // generated from `descriptions.tags`
  item_data:        String          // generated from `descriptions.market_actions`
)
