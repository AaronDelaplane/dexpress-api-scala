package clients

import cats.effect.IO
import cats.implicits._
import cats.{Monoid, Show}
import common.{SteamAsset, SteamDescription, SteamInventory, SteamMarketAction, SteamTag}
import io.circe.{Decoder, Encoder}
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

package object steam {

  /*
  show instances -------------------------------------------------------------------------------------------------------
   */
  // generic for List[A]
  implicit class ShowList[A](as: List[A])(implicit s: Show[A]) {
    def listShow: Show[List[A]] = Show.show(_.foldLeft(Monoid[String].empty)((b, a) => b + a.show))
  }

  /*
  codec instances ------------------------------------------------------------------------------------------------------ 
   */
  implicit def steamInventoryDecoder: Decoder[SteamInventory] =
    Decoder.forProduct2("assets", "descriptions")(SteamInventory.apply)
  implicit def steamInventoryEncoder: Encoder[SteamInventory] =
    Encoder.forProduct2("assets", "descriptions")(x => (x.descriptions, x.assets))
  implicit def steamInventoryEntityDecoder: EntityDecoder[IO, SteamInventory] =
    jsonOf[IO, SteamInventory]

  implicit def steamAssetDecoder: Decoder[SteamAsset] =
    Decoder.forProduct6("appid","contextid","assetid","classid","instanceid","amount")(SteamAsset.apply)
  implicit def steamAssetEncoder: Encoder[SteamAsset] =
    Encoder.forProduct6("appid","contextid","assetid","classid","instanceid","amount")(
      x => (x.appid, x.contextid, x.assetid, x.classid, x.instanceid, x.amount)
    )

  implicit def steamDescriptionDecoder: Decoder[SteamDescription] =
    Decoder.forProduct9(
      "appid",
      "classid",
      "instanceid",
      "icon_url",
      "tradable",
      "type",
      "market_hash_name",
      "tags",
      "market_actions"
    )(SteamDescription.apply)
  implicit def steamDescriptionEncoder: Encoder[SteamDescription] =
    Encoder.forProduct9(
      "appid",
      "classid",
      "instanceid",
      "icon_url",
      "tradable",
      "type",
      "market_hash_name",
      "tags",
      "market_actions"
    )(x => (
      x.appid,
      x.classid,
      x.instanceid,
      x.icon_url,
      x.tradable,
      x.`type`,
      x.market_hash_name,
      x.tags,
      x.market_actions
    )
    )

  implicit def steamTagDecoder: Decoder[SteamTag] =
    Decoder.forProduct2("category", "localized_tag_name")(SteamTag.apply)
  implicit def steamTagEncoder: Encoder[SteamTag] =
    Encoder.forProduct2("category", "localized_tag_name")(x => (x.category, x.localized_tag_name))

  implicit def steamMarketActionDecoder: Decoder[SteamMarketAction] =
    Decoder.forProduct1("link")(SteamMarketAction.apply)
  implicit def steamMarketActionEncoder: Encoder[SteamMarketAction] =
    Encoder.forProduct1("link")(x => (x.link))
}
