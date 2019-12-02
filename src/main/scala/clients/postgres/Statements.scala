package clients.postgres

import java.util.UUID

import cats.data.NonEmptyList
import datatypes.Asset
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

object Statements {
    
  implicit val han = LogHandler.jdkLogHandler  
  
  def insertAssets(as: NonEmptyList[Asset]): Update[Asset] =
    Update[Asset]("""
      insert into assets (
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
    
    def selectAsset(assedId: UUID): Query0[Asset] =
      sql"select * from assets where id = $assedId".query[Asset]
    
    def updateAssetTradingState(assetId: UUID, tradingState: Boolean): Update0 =
      sql"update assets set trading = $tradingState where id = $assetId".update  
  
}
