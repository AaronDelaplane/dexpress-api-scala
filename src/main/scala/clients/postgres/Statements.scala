package clients.postgres

import java.util.UUID

import cats.data.NonEmptyList
import datatypes.AssetDataA
import doobie._
import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._

object Statements {
    
  implicit val han = LogHandler.jdkLogHandler  
  
  def insertAssets(as: NonEmptyList[AssetDataA]): Update[AssetDataA] =
    Update[AssetDataA]("""
      insert into assets_data_a (
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
        sticker_info
      ) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """)
    
    def selectAssetDataA(assedId: UUID): Query0[AssetDataA] =
      sql"select * from assets_data_a where id = $assedId".query[AssetDataA]
    
    def updateAssetTradingState(assetId: UUID, tradingState: Boolean): Update0 =
      sql"update assets set trading = $tradingState where id = $assetId".update  
  
}
