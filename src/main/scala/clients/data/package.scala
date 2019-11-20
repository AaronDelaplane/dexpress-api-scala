package clients

import java.util.UUID

import cats.Show

package object data {

  final case class Asset(
    id:               UUID,
    refresh_id:       UUID,
    steam_id:         String,
    appid:            Int,
    assetid:          String,
    classid:          String,
    instanceid:       String,
    tradable:         Int,
    market_hash_name: String,
    icon_url:         String,
    asset_type:       String,         // `type` in steam response
    exterior:         Option[String], // generated from `descriptions.tags`. not present on all
    rarity:           String,         // generated from `descriptions.tags`
    item_data:        String,         // generated from `descriptions.market_actions`
    sticker_info:     String
  )
  
  /*
  show instances -------------------------------------------------------------------------------------------------------
   */
  implicit def assetShow: Show[Asset] = Show.show(x =>
    s"""
       |id:               ${x.id}
       |refresh_id:       ${x.refresh_id}
       |steam_id:         ${x.steam_id}
       |appid:            ${x.appid}
       |assetid:          ${x.assetid}
       |classid:          ${x.classid}
       |instanceid:       ${x.instanceid}
       |tradable:         ${x.tradable}
       |market_hash_name: ${x.market_hash_name}
       |icon_url:         ${x.icon_url}
       |asset_type:       ${x.asset_type}
       |exterior:         ${x.exterior}
       |rarity:           ${x.rarity}
       |item_data:        ${x.item_data}
       |sticker_info:     ${x.sticker_info}
    """.stripMargin
  )
}
