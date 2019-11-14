package clients

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

package object steam {
  /*
  inventory
   */
  final case class Inventory(
      assets: Option[List[Asset]],
      descriptions: Option[List[Description]]
  )

  object Inventory {
    implicit def inventoryDecoder: Decoder[Inventory] =
      Decoder.forProduct2("assets", "descriptions")(Inventory.apply)

    implicit def inventoryEncoder: Encoder[Inventory] =
      Encoder.forProduct2("assets", "descriptions")(
        i => (i.descriptions, i.assets)
      )

    implicit def inventoryEntityDecoder: EntityDecoder[IO, Inventory] =
      jsonOf[IO, Inventory]

    implicit def inventoryEntityEncoder: EntityEncoder[IO, Inventory] =
      jsonEncoderOf[IO, Inventory]
  }

  final case class Asset(
      appid: Option[Int],
      contextid: Option[String],
      assetid: Option[String],
      classid: Option[String],
      instanceid: Option[String],
      amount: Option[String]
  )

  object Asset {
    implicit def assetDecoder: Decoder[Asset] =
      Decoder.forProduct6(
        "appid",
        "contextid",
        "assetid",
        "classid",
        "instanceid",
        "amount"
      )(Asset.apply)

    implicit def assetEncoder: Encoder[Asset] =
      Encoder.forProduct6(
        "appid",
        "contextid",
        "assetid",
        "classid",
        "instanceid",
        "amount"
      )(
        a =>
          (a.appid, a.contextid, a.assetid, a.classid, a.instanceid, a.amount)
      )
  }

  /*
  description
   */
  final case class Description(
      appid: Option[Int],
      classid: Option[String],
      instanceid: Option[String],
      icon_url: Option[String],
      tradable: Option[Int],
      `type`: Option[String],
      market_hash_name: Option[String],
      tags: List[Tag]
  )

  object Description {
    implicit def descriptionDecoder: Decoder[Description] =
      Decoder.forProduct8(
        "appid",
        "classid",
        "instanceid",
        "icon_url",
        "tradable",
        "type",
        "market_hash_name",
        "tags"
      )(Description.apply)

    implicit def descriptionEncoder: Encoder[Description] =
      Encoder.forProduct8(
        "appid",
        "classid",
        "instanceid",
        "icon_url",
        "tradable",
        "type",
        "market_hash_name",
        "tags"
      )(
        d =>
          (
            d.appid,
            d.classid,
            d.instanceid,
            d.icon_url,
            d.tradable,
            d.`type`,
            d.market_hash_name,
            d.tags
          )
      )
  }

  /*
  tag
   */
  final case class Tag(
      category: Option[String],
      localized_tag_name: Option[String]
  )
  object Tag {
    implicit def tagDecoder: Decoder[Tag] =
      Decoder.forProduct2("category", "localized_tag_name")(Tag.apply)

    implicit def tagEncoder: Encoder[Tag] =
      Encoder.forProduct2("category", "localized_tag_name")(
        t => (t.category, t.localized_tag_name)
      )
  }
}
