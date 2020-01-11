package dexpress

import java.util.UUID

import cats.effect.IO
import cats.implicits._
import cats.kernel.Monoid
import ciris.{ConfigDecoder, ConfigError, Secret}
import doobie.Meta
import eu.timepit.refined.types.net.UserPortNumber
import io.circe.Encoder._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import org.http4s.circe._
import org.http4s.dsl.impl.ValidatingQueryParamDecoderMatcher
import org.http4s.{EntityDecoder, EntityEncoder, ParseFailure, QueryParamDecoder, Uri}
import dexpress.enums._
import dexpress.types._

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
  various codecs
   */
  implicit def assetEncoder: Encoder[Asset] = (x: Asset) => Json.obj(
    ("id_asset", x.id_asset.asJson),
    ("trading", x.trading.asJson),
    ("steam_id", x.steam_id.asJson),
    ("floatvalue", x.floatvalue.asJson),
    ("classid", x.classid.asJson),
    ("instanceid", x.instanceid.asJson),
    ("appid", x.appid.asJson),
    ("assetid", x.assetid.asJson),
    ("amount", x.amount.asJson),
    ("market_hash_name", x.market_hash_name.asJson),
    ("icon_url", x.icon_url.asJson),
    ("tradable", x.tradable.asJson),
    ("type", x.`type`.asJson),
    ("link_id", x.link_id.asJson),
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
  
  implicit def nelAssetEntityEncoder: EntityEncoder[IO, NEL[Asset]] = jsonEncoderOf[IO, NEL[Asset]]
  
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

  implicit def inventoryActionQPD: QueryParamDecoder[ActionAssets] =
    QueryParamDecoder[String].emap[ActionAssets](ActionAssets.withNameLowercaseOnlyOption(_).fold(
      ParseFailure(s"action must be one of: ${ActionAssets.values}", Monoid[String].empty).asLeft[ActionAssets]
    )(_.asRight[ParseFailure]))
  
  implicit def uuidQPD: QueryParamDecoder[UUID] =
    QueryParamDecoder[String].emap[UUID](s => uuidRegex.findFirstIn(s).fold(
      ParseFailure(s"not a valid uuid: $s", Monoid[String].empty).asLeft[UUID]
    )(UUID.fromString(_).asRight[ParseFailure]))
  
  /*
  validating query param decoder matchers ------------------------------------------------------------------------------
   */
  object AssetIdQPM extends ValidatingQueryParamDecoderMatcher[UUID]("assetid")
  
  object CountQPM extends ValidatingQueryParamDecoderMatcher[Count]("count")
  
  object InventoryActionQPM extends ValidatingQueryParamDecoderMatcher[ActionAssets]("action")

  object TradingQPM extends ValidatingQueryParamDecoderMatcher[Boolean]("trading")

  /*
  doobie metas -----------------------------------------------------------------------------------------------------------
   */
  implicit val uriMeta: Meta[Uri] = Meta[String].timap(Uri.unsafeFromString)(_.renderString)

  implicit val uriListString: Meta[List[Uri]] =
    Meta[Array[String]].timap[List[Uri]](_.toList.map(Uri.unsafeFromString))(_.map(_.renderString).toArray)

}
