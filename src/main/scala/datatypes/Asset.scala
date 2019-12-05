package datatypes

import java.util.UUID

final case class Asset(
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
  /*
   todo can an asset have more than one sticker?
   "<br><div id=\"sticker_info\" name=\"sticker_info\" title=\"Sticker\" style=\"border: 2px solid rgb(102, 102, 102); border-radius: 6px; width=100; margin:4px; padding:8px;\"><center><img width=64 height=48 src=\"https://steamcdn-a.akamaihd.net/apps/730/icons/econ/stickers/emskatowice2014/wolf_esl_foil.84e4f1689cdeb30d227745d6ce1299d24770c5a9.png\"><img width=64 height=48 src=\"https://steamcdn-a.akamaihd.net/apps/730/icons/econ/stickers/emskatowice2014/wolf_esl_foil.84e4f1689cdeb30d227745d6ce1299d24770c5a9.png\"><img width=64 height=48 src=\"https://steamcdn-a.akamaihd.net/apps/730/icons/econ/stickers/emskatowice2014/wolf_esl_foil.84e4f1689cdeb30d227745d6ce1299d24770c5a9.png\"><img width=64 height=48 src=\"https://steamcdn-a.akamaihd.net/apps/730/icons/econ/stickers/emskatowice2014/wolf_esl_foil.84e4f1689cdeb30d227745d6ce1299d24770c5a9.png\"><br>Sticker: ESL Wolf (Foil) | Katowice 2014, ESL Wolf (Foil) | Katowice 2014, ESL Wolf (Foil) | Katowice 2014, ESL Wolf (Foil) | Katowice 2014</center></div>" 
   */
  sticker_info:     String
  //trading:          Boolean
)

object Asset {
  def fromMaybe(a: MaybeAsset): Option[Asset] =
    a.tradable match {
      case false => None
      case true  => Some(
                      Asset(
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
                        sticker_info     = a.sticker_info
                        //trading          = false
                      )
                    )
    }
}
