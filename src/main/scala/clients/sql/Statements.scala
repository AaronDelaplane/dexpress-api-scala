package clients.sql

import cats.data.NonEmptyList
import clients.data.Asset
import doobie._
import doobie.free.connection.ConnectionIO
import doobie.postgres._
import doobie.postgres.implicits._

object Statements {
  
  def insertAssets(assets: NonEmptyList[Asset]): ConnectionIO[Int] =
    Update[Asset]("""
      insert into assets (
        id,
        refresh_id,
        steam_id,
        appid,
        assetid,
        classid,
        instanceid,
        tradable,
        market_hash_name,
        icon_url,
        asset_type,
        exterior,
        rarity,
        item_data,
        sticker_info
      ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """).updateMany(assets)
  
}
