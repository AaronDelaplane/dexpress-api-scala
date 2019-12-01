package datatypes

import java.util.UUID

final case class TradableAsset(
  id:               UUID,
  refresh_id:       UUID,
  steam_id:         String,
  appid:            Int,
  assetid:          String,
  classid:          String,
  instanceid:       String,
  market_hash_name: String,
  icon_url:         String,
  asset_type:       String,         // `type` in steam response
  exterior:         Option[String], // generated from `descriptions.tags`. not present on all
  rarity:           String,         // generated from `descriptions.tags`
  link_id:          Option[String], // generated from `descriptions.market_actions[index].link`. not present on all descriptions
  sticker_info:     String,
  trading:          Boolean
)

object TradableAsset {
  def fromMaybe(a: MaybeTradableAsset): Option[TradableAsset] =
    a.tradable match {
      case false => None
      case true  => Some(
                      TradableAsset(
                        id               = a.id,
                        refresh_id       = a.refresh_id,
                        steam_id         = a.steam_id,
                        appid            = a.appid,
                        assetid          = a.assetid,
                        classid          = a.classid,
                        instanceid       = a.instanceid,
                        market_hash_name = a.market_hash_name,
                        icon_url         = a.icon_url,
                        asset_type       = a.asset_type,
                        exterior         = a.exterior,
                        rarity           = a.rarity,
                        link_id          = a.link_id,
                        sticker_info     = a.sticker_info,
                        trading          = false
                      )
                    )
    }
}
