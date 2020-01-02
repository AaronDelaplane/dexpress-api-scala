package service.clients.postgres

import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import service.codecs._
import service.types._

object Statements {
    
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  
  /*
  statements -----------------------------------------------------------------------------------------------------------
   */
  def delete(iR: IdRefresh): Update0 =
    sql"delete from assets where id_refresh = ${iR.value}".update
  
  def insertAssets: Update[Asset] =
    Update[Asset]("""
      insert into assets (
        id_asset,
        id_refresh,
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
  
  def insertEventRefreshAssets(iR: IdRefresh, iS: IdSteam, time: Long): Update0 =
    sql"insert into events_refresh_assets(id_refresh, steam_id, time) values (${iR.value}, ${iS.value}, $time)".update
  
  def selectAsset(iA: IdAsset): Query0[Asset] =
    sql"select * from assets where id_asset = ${iA.value}".query[Asset]

  def selectAssets(iR: IdRefresh): Query0[Asset] =
    sql"select * from assets where id_refresh = ${iR.value}".query[Asset]

  def selectEventsRefreshAssets(iS: IdSteam): Query0[EventRefreshAssets] =
    sql"select * from events_refresh_assets where steam_id = ${iS.value}".query[EventRefreshAssets]

  def updateAsset(iA: IdAsset, sT: StateTrading): Update0 =
    sql"update assets set trading = ${sT.value} where id_asset = ${iA.value}".update
  
  def updateAsset(iA: IdAsset, sT: StateTrading, fV: FloatValue): Update0 =
    sql"update assets set trading = ${sT.value}, floatvalue = ${fV.value} where id_asset = ${iA.value}".update
  
}
