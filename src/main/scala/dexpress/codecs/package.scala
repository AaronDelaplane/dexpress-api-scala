package dexpress

import java.util.UUID

import cats.effect.IO
import cats.implicits._
import cats.kernel.Monoid
import ciris.{ConfigDecoder, ConfigError, Secret}
import dexpress.types._
import doobie.Meta
import eu.timepit.refined.types.net.UserPortNumber
import io.circe.Encoder._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import mouse.boolean._
import org.http4s.circe._
import org.http4s.dsl.impl.{OptionalValidatingQueryParamDecoderMatcher, ValidatingQueryParamDecoderMatcher}
import org.http4s.{EntityDecoder, EntityEncoder, ParseFailure, QueryParamDecoder, Uri}

import scala.util.Try
import scala.util.matching.Regex

package object codecs {
  import doobie.postgres.implicits._
  
  /*
  steam codecs ---------------------------------------------------------------------------------------------------------
   */
  implicit def steamAssetDecoder: Decoder[SteamAsset] =
    Decoder.forProduct6("classid", "instanceid", "appid", "contextid", "assetid", "amount")(SteamAsset.apply)
  implicit def steamAssetEncoder: Encoder[SteamAsset] =
    Encoder.forProduct6("classid", "instanceid", "appid", "contextid", "assetid", "amount")(
      x => (x.appid, x.contextid, x.assetid, x.classid, x.instanceid, x.amount))

  implicit def steamDescriptionDecoder: Decoder[SteamDescription] =
    Decoder.forProduct10(
      "classid", "instanceid", "appid", "icon_url", "tradable", "type", "market_hash_name", "tags", "market_actions", "descriptions"
    )(SteamDescription.apply)
  implicit def steamDescriptionEncoder: Encoder[SteamDescription] =
    Encoder.forProduct10(
      "classid", "instanceid", "appid", "icon_url", "tradable", "type", "market_hash_name", "tags", "market_actions", "descriptions"
    )(x => (x.appid, x.classid, x.instanceid, x.icon_url, x.tradable, x.`type`, x.market_hash_name, x.tags, x.market_actions, x.descriptions))
  
  implicit def steamInventoryDecoder: Decoder[SteamInventory] =
    Decoder.forProduct2("assets", "descriptions")(SteamInventory.apply)
  implicit def steamInventoryEncoder: Encoder[SteamInventory] =
    Encoder.forProduct2("assets", "descriptions")(x => (x.descriptions, x.assets))
  implicit def steamInventoryEntityDecoder: EntityDecoder[IO, SteamInventory] =
    jsonOf[IO, SteamInventory]

  implicit def steamMarketActionDecoder: Decoder[SteamMarketAction] =
    Decoder.forProduct1("link")(SteamMarketAction.apply)
  implicit def steamMarketActionEncoder: Encoder[SteamMarketAction] =
    Encoder.forProduct1("link")(x => (x.link))

  implicit def steamTagDecoder: Decoder[SteamTag] =
    Decoder.forProduct5("category", "internal_name", "localized_category_name", "localized_tag_name", "color")(SteamTag.apply)
  implicit def steamTagEncoder: Encoder[SteamTag] =
    Encoder.forProduct5(
      "category", "internal_name", "localized_category_name", "localized_tag_name", "color"
    )(x => (x.category, x.internal_name, x.localized_category_name, x.localized_tag_name, x.color))
   
  implicit def steamNestedDescriptionDecoder: Decoder[SteamNestedDescription] =
    Decoder.forProduct2("type", "value")(SteamNestedDescription.apply)
  implicit def steamNestedDescriptionEncoder: Encoder[SteamNestedDescription] =
    Encoder.forProduct2("type", "value")(x => (x.`type`, x.value))
  
  /*
  asset codecs ---------------------------------------------------------------------------------------------------------
   */
  implicit def assetEncoder: Encoder[Asset] = (x: Asset) => Json.obj(
    ("id_asset", x.id_asset.asJson),
    ("id_user", x.id_user.asJson),
    ("id_refresh", x.id_refresh.asJson),
    ("is_trading", x.is_trading.asJson),
    ("id_user_steam", x.id_user_steam.asJson),
    ("float_value", x.float_value.asJson),
    ("id_class", x.id_class.asJson),
    ("id_instance", x.id_instance.asJson),
    ("id_app", x.id_app.asJson),
    ("id_asset_steam", x.id_asset_steam.asJson),
    ("amount", x.amount.asJson),
    ("market_hash_name", x.market_hash_name.asJson),
    ("icon_url", x.icon_url.asJson),
    ("type_asset", x.type_asset.asJson),
    ("id_link", x.id_link.asJson),
    ("sticker_urls", x.sticker_urls.asJson),
    ("tag_exterior_category", x.tag_exterior_category.asJson),
    ("tag_exterior_internal_name", x.tag_exterior_internal_name.asJson),
    ("tag_exterior_localized_category_name", x.tag_exterior_localized_category_name.asJson),
    ("tag_exterior_localized_tag_name", x.tag_exterior_localized_tag_name.asJson),
    ("tag_rarity_category", x.tag_rarity_category.asJson),
    ("tag_rarity_internal_name", x.tag_rarity_internal_name.asJson),
    ("tag_rarity_localized_category_name", x.tag_rarity_localized_category_name.asJson),
    ("tag_rarity_localized_tag_name", x.tag_rarity_localized_tag_name.asJson),
    ("tag_rarity_color", x.tag_rarity_color.asJson),
    ("tag_type_category", x.tag_type_category.asJson),
    ("tag_type_internal_name", x.tag_type_internal_name.asJson),
    ("tag_type_localized_category_name", x.tag_type_localized_category_name.asJson),
    ("tag_type_localized_tag_name", x.tag_type_localized_tag_name.asJson),
    ("tag_weapon_category", x.tag_weapon_category.asJson),
    ("tag_weapon_internal_name", x.tag_weapon_internal_name.asJson),
    ("tag_weapon_localized_category_name", x.tag_weapon_localized_category_name.asJson),
    ("tag_weapon_localized_tag_name", x.tag_weapon_localized_tag_name.asJson),
    ("tag_quality_category", x.tag_quality_category.asJson),
    ("tag_quality_internal_name", x.tag_quality_internal_name.asJson),
    ("tag_quality_localized_category_name", x.tag_quality_localized_category_name.asJson),
    ("tag_quality_localized_tag_name", x.tag_quality_localized_tag_name.asJson),
    ("tag_quality_color", x.tag_quality_color.asJson),
  )
  
  implicit def assetEntityEncoder: EntityEncoder[IO, Asset] = jsonEncoderOf[IO, Asset]
  
  implicit def listAssetEntityEncoder: EntityEncoder[IO, List[Asset]] = jsonEncoderOf[IO, List[Asset]]
  
  implicit def nelAssetEntityEncoder: EntityEncoder[IO, NEL[Asset]] = jsonEncoderOf[IO, NEL[Asset]]
  
  /*
  user codecs ----------------------------------------------------------------------------------------------------------
   */
  implicit def userEncoder: Encoder[User] = 
    Encoder.forProduct3("id_user", "id_user_steam", "name_first")(x => (x.id_user, x.id_user_steam, x.name_first))
  
  implicit def userEntityEncoder: EntityEncoder[IO, User] = jsonEncoderOf[IO, User]
  
  implicit def requestPostUserDecoder: Decoder[RequestPostUser] =
    Decoder.forProduct2("id_user_steam", "name_first")(RequestPostUser.apply)
  
  implicit def requestPostUserEntityDecoder: EntityDecoder[IO, RequestPostUser] =
    jsonOf[IO, RequestPostUser]
  
  /*
  various codecs -------------------------------------------------------------------------------------------------------
   */
  implicit def booleanEntityEncoder: EntityEncoder[IO, Boolean] = jsonEncoderOf[IO, Boolean]
  
  /*
  config codecs --------------------------------------------------------------------------------------------------------
   */
  implicit def portDecoder: ConfigDecoder[String, UserPortNumber] =
    ConfigDecoder.lift[String, UserPortNumber](string =>
      Try(string.toInt).fold(
        e => Left(ConfigError(e.getMessage)),
        n => UserPortNumber.from(n).fold(s => Left(ConfigError(s)), Right.apply)))

  implicit def secretDecoder: ConfigDecoder[String, Secret[String]] =
    ConfigDecoder.lift[String, Secret[String]](s => Right(Secret(s)))

  implicit def uriDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder.lift[String, Uri](s => Uri.fromString(s).fold(e => Left(ConfigError(e.message)), Right.apply))
  
  /*
  query param codecs ---------------------------------------------------------------------------------------------------
   */
  val r = Range.inclusive(1, 1000)
  val uuidRegex = new Regex(".*[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
    
  implicit def countQPD: QueryParamDecoder[Count] =
    QueryParamDecoder[Int].emap[Count](n => r.contains(n) match {
      case true  => Count(n).asRight[ParseFailure]
      case false => ParseFailure(s"count must be ${r.start} to ${r.end}", Monoid[String].empty).asLeft[Count]})

  implicit def searchFilter: QueryParamDecoder[SearchFilter] =
    QueryParamDecoder[String].emap[SearchFilter](toUUID(_)("filter").map(SearchFilter.apply))

  implicit def searchFilterNot: QueryParamDecoder[SearchFilterNot] =
    QueryParamDecoder[String].emap[SearchFilterNot](toUUID(_)("filternot").map(SearchFilterNot.apply))
  
  implicit def searchIsTrading: QueryParamDecoder[StateTrading] =
    QueryParamDecoder[String].emap(
      _.toBooleanOption
        .fold(
          ParseFailure("istrading must be a boolean", Monoid[String].empty).asLeft[StateTrading]
        )(
          StateTrading(_).asRight[ParseFailure]
        ))
  
  implicit def searchLimitQPD: QueryParamDecoder[SearchLimit] =
    QueryParamDecoder[Int].emap(n => 
       if ((0 to 1000).contains(n)) SearchLimit(n).asRight[ParseFailure]
       else ParseFailure("limit must be within 0 to 100", Monoid[String].empty).asLeft[SearchLimit]
    )

  implicit def searchOffsetQPD: QueryParamDecoder[SearchOffset] =
    QueryParamDecoder[Int].emap(n =>
      if ((0 to 1000).contains(n)) SearchOffset(n).asRight[ParseFailure]
      else ParseFailure("offset must be within 0 to 250", Monoid[String].empty).asLeft[SearchOffset])
  
  implicit def uuidQPD: QueryParamDecoder[UUID] =
    QueryParamDecoder[String].emap[UUID](toUUID(_)(""))

  implicit def idAssetQPD: QueryParamDecoder[IdAsset] =
    QueryParamDecoder[String].emap(toUUID(_)("idasset")).map(IdAsset.apply)
  
  implicit def idUserQPD: QueryParamDecoder[IdUser] =
    QueryParamDecoder[String].emap(toUUID(_)("iduser")).map(IdUser.apply)
  
  implicit def idUserSteamQPD: QueryParamDecoder[IdUserSteam] =
      QueryParamDecoder[String].emap(s => 
        s.nonEmpty.fold(
          IdUserSteam(s).asRight[ParseFailure], 
          ParseFailure("idusersteam must be non-empty", "").asLeft[IdUserSteam]
        )
      )
  
  private def toUUID(s: String)(queryParameterName: String): Either[ParseFailure, UUID] =
    uuidRegex
      .findFirstIn(s)
      .fold(
        ParseFailure(s"$queryParameterName must be a valid uuid", Monoid[String].empty).asLeft[UUID]
      )(
        UUID.fromString(_).asRight[ParseFailure]
      )
  
  /*
  validating query param decoder matchers ------------------------------------------------------------------------------
   */
  object CountQPM extends ValidatingQueryParamDecoderMatcher[Count]("count")

  object StateTradingQPM extends ValidatingQueryParamDecoderMatcher[StateTrading]("trading")
  
  object SearchOffsetQPM extends ValidatingQueryParamDecoderMatcher[SearchOffset]("offset")
  
  object SearchLimitQPM extends ValidatingQueryParamDecoderMatcher[SearchLimit]("limit")
  
  object MaybeSearchFilterQPM extends OptionalValidatingQueryParamDecoderMatcher[SearchFilter]("filter")
  
  object MaybeSearchFilterNotQPM extends OptionalValidatingQueryParamDecoderMatcher[SearchFilterNot]("filternot")
  
  object IdAssetQPM extends ValidatingQueryParamDecoderMatcher[IdAsset]("idasset")
  
  object IdUserQPM extends ValidatingQueryParamDecoderMatcher[IdUser]("iduser")
  
  object IdUserSteamQPM extends ValidatingQueryParamDecoderMatcher[IdUserSteam]("idusersteam")

  /*
  doobie metas -----------------------------------------------------------------------------------------------------------
   */
  implicit val uriMeta: Meta[Uri] = Meta[String].timap(Uri.unsafeFromString)(_.renderString)

  implicit val uriListString: Meta[List[Uri]] =
    Meta[Array[String]].timap[List[Uri]](_.toList.map(Uri.unsafeFromString))(_.map(_.renderString).toArray)

}
