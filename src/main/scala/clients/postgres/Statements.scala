package clients.postgres

import java.util.UUID

import cats.data.NonEmptyList
import datatypes.TradableAsset
import doobie._
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

object Statements {
    
  implicit val han = LogHandler.jdkLogHandler  
  
  def insertAssets(as: NonEmptyList[TradableAsset]): Update[TradableAsset] =
    Update[TradableAsset]("""
      insert into tradable_assets (
        id,
        refresh_id,
        steam_id,
        appid,
        assetid,
        classid,
        instanceid,
        market_hash_name,
        icon_url,
        asset_type,
        exterior,
        rarity,
        link_id,
        sticker_info,
        trading
      ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """)
    
    def selectAsset(assedId: UUID): Query0[TradableAsset] =
      sql"select * from tradable_assets where id = $assedId".query[TradableAsset]
    
    def updateAssetTradingState(assetId: UUID, tradingState: Boolean): Update0 =
      sql"update tradable_assets set trading = $tradingState where id = $assetId".update  
  
}
