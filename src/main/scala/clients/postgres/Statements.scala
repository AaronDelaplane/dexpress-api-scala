package clients.postgres

import java.util.UUID

import datatypes._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import org.http4s.Uri

object Statements {
    
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  
  implicit val uriMeta: Meta[Uri] = Meta[String].timap(Uri.unsafeFromString)(_.renderString)
  
  implicit val uriListString: Meta[List[Uri]] = 
    Meta[Array[String]].timap[List[Uri]](_.toList.map(Uri.unsafeFromString))(_.map(_.renderString).toArray)
  
  def insertAssets(as: List[Asset]): Update[Asset] =
    Update[Asset]("""
      insert into assets (
          dexpress_asset_id,
          refresh_id,
          trading,
          steam_id,
          floatvalue,
          classid,
          instanceid,
          appid,
          assetid,  
          amount,
          market_hash_name,
          icon_url,
          tradable,
          type,
          link_id,
          sticker_urls,
          tag_exterior_category,
          tag_exterior_internal_name,
          tag_exterior_localized_category_name,
          tag_exterior_localized_tag_name,
          tag_rarity_category,
          tag_rarity_internal_name,
          tag_rarity_localized_category_name,
          tag_rarity_localized_tag_name,
          tag_rarity_color,
          tag_type_category,
          tag_type_internal_name,
          tag_type_localized_category_name,
          tag_type_localized_tag_name,
          tag_weapon_category,
          tag_weapon_internal_name,
          tag_weapon_localized_category_name,
          tag_weapon_localized_tag_name,
          tag_quality_category,
          tag_quality_internal_name,
          tag_quality_localized_category_name,
          tag_quality_localized_tag_name,
          tag_quality_color
      ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
    """)
    
    def selectAsset(assedId: UUID): Query0[Asset] =
      sql"select * from assets where id = $assedId".query[Asset]
  
//    def insertAssetDataB: Update[AssetDataB] =
//      Update[AssetDataB]("""
//        insert into assets_data_b (dexpress_asset_id, float_value) values (?,?)
//      """)
    
//    def updateAssetTradingState(assetId: UUID, tradingState: Boolean): Update0 =
//      sql"update assets set trading = $tradingState where id = $assetId".update  
  
}
