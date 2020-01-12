package dexpress.clients.postgres

import dexpress.types._
import doobie._
import doobie.implicits._
import doobie.postgres.implicits._
import dexpress.codecs._
/*
required imports that IntelliJ's optimize imports command will remove:
import doobie.postgres.implicits._
import dexpress.codecs._
 */

// format: on

object Statements {
    
  implicit val han: LogHandler = LogHandler.jdkLogHandler
  
  /*
  statements -----------------------------------------------------------------------------------------------------------
   */
  def delete(iR: IdRefresh): Update0 =
    sql"delete from assets where id_refresh = ${iR.value}".update
  
  def exists(iUS: IdUserSteam): Query0[Boolean] =
    sql"select exists (select * from users where id_user_steam = ${iUS.value})".query[Boolean]
  
  def insertAssets: Update[Asset] =
    Update[Asset](s"""
      insert into assets (
        id_asset,
        id_user,
        id_refresh,
        is_trading,
        id_user_steam,
        float_value,
        id_class,
        id_instance,
        id_app,
        id_asset_steam,  
        amount,
        market_hash_name,
        icon_url,
        type_asset,
        id_link,
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
  
  def insertEventAssetsRefresh(iR: IdRefresh, iU: IdUser, time: Long): Update0 =
    sql"insert into events_assets_refresh(id_refresh, id_user, time) values (${iR.value}, ${iU.value}, $time)".update
  
  def insertUser(u: User): Update0 = 
    sql"insert into users (id_user, id_user_steam, name_first) values (${u.id_user}, ${u.id_user_steam}, ${u.name_first})".update
  
  def selectAsset(iA: IdAsset): Query0[Asset] =
    sql"select * from assets where id_asset = ${iA.value}".query[Asset]

  def selectAssets(sT: StateTrading): Query0[Asset] =
    sql"select * from assets where is_trading = ${sT.value}".query[Asset]
  
  // note: selecting by id_refresh or id_user results in the same set given former refreshes for a given user are
  // currently deleted. If in the future refreshes are _not_ deleted than id_refresh _must_ be used. For the time
  // being it doesn't really matter.
  def selectAssetsFilter(sT: StateTrading, iR: IdRefresh): Query0[Asset] =
    sql"select * from assets where is_trading = ${sT.value} and id_refresh = ${iR.value}".query[Asset]
  
  def selectAssetsFilterNot(sT: StateTrading, iU: IdUser): Query0[Asset] =
    sql"select * from assets where is_trading = ${sT.value} and id_user != ${iU.value}".query[Asset]

  def selectEventsAssetsRefresh(iU: IdUser): Query0[EventAssetsRefresh] =
    sql"select * from events_assets_refresh where id_user = ${iU.value}".query[EventAssetsRefresh]

  def selectUser(iU: IdUser): Query0[User] =
    sql"select * from users where id_user = ${iU.value}".query[User]
  
  def selectUser(iUS: IdUserSteam): Query0[User] =
    sql"select * from users where id_user_steam = ${iUS.value}".query[User]
  
  def updateAsset(iA: IdAsset, sT: StateTrading): Update0 =
    sql"update assets set is_trading = ${sT.value} where id_asset = ${iA.value}".update
  
  def updateAsset(iA: IdAsset, sT: StateTrading, fV: FloatValue): Update0 =
    sql"update assets set is_trading = ${sT.value}, float_value = ${fV.value} where id_asset = ${iA.value}".update
  
}
