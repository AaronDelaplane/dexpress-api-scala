import java.util.UUID

import cats.effect.IO
import cats.implicits._
import cats.kernel.Monoid
import ciris.{ConfigDecoder, ConfigError, Secret}
import datatypes._
import enums._
import eu.timepit.refined.types.net.UserPortNumber
import io.circe.{Decoder, Encoder}
import org.http4s.circe.jsonOf
import org.http4s.dsl.impl.ValidatingQueryParamDecoderMatcher
import org.http4s.{EntityDecoder, ParseFailure, QueryParamDecoder, Uri}

import scala.util.Try
import scala.util.matching.Regex

package object codecs {
  
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

  implicit def inventoryActionQPD: QueryParamDecoder[ActionInventory] =
    QueryParamDecoder[String].emap[ActionInventory](ActionInventory.withNameLowercaseOnlyOption(_).fold(
      ParseFailure(s"action must be one of: ${ActionInventory.values}", Monoid[String].empty).asLeft[ActionInventory]
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
  
  object InventoryActionQPM extends ValidatingQueryParamDecoderMatcher[ActionInventory]("action")

  object TradingQPM extends ValidatingQueryParamDecoderMatcher[Boolean]("trading")

}
