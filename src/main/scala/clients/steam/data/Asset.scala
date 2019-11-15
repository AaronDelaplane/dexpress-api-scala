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
  exterior:         String, // generated from `descriptions.tags`
  rarity:           String, // generated from `descriptions.tags`
  item_data:        String  // generated from `descriptions.market_actions`
)
