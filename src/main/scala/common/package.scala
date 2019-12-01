import cats.Show
import cats.data.NonEmptyList
import cats.implicits._
import ciris.{ConfigDecoder, ConfigError, Secret}
import eu.timepit.refined.types.net.UserPortNumber
import org.http4s.Uri

import scala.util.Try

package object common {
  
  /*
  types ----------------------------------------------------------------------------------------------------------------
   */
  type NEL[A]     = NonEmptyList[A]
  type ErrorOr[A] = Either[String, A]
  
  /*
  show instances -------------------------------------------------------------------------------------------------------
   */
  implicit def assetShow: Show[Asset] = Show.show(x =>
    s"""
       |id:               ${x.id}
       |refresh_id:       ${x.refresh_id}
       |steam_id:         ${x.steam_id}
       |appid:            ${x.appid}
       |assetid:          ${x.assetid}
       |classid:          ${x.classid}
       |instanceid:       ${x.instanceid}
       |tradable:         ${x.tradable}
       |market_hash_name: ${x.market_hash_name}
       |icon_url:         ${x.icon_url}
       |asset_type:       ${x.asset_type}
       |exterior:         ${x.exterior}
       |rarity:           ${x.rarity}
       |link_id:          ${x.link_id}
       |sticker_info:     ${x.sticker_info}
    """.stripMargin
  )
  
  /*
  show instances ------------------------------------------------------------------------------------------------------- 
   */
  implicit def nelStringShow: Show[NonEmptyList[String]] = Show.show[NonEmptyList[String]](nel =>
    nel.foldLeft("\n")(_ + _ + "\n")
  ) 
  
  /*
  codecs ---------------------------------------------------------------------------------------------------------------
   */
  implicit def portDecoder: ConfigDecoder[String, UserPortNumber] =
    ConfigDecoder.lift[String, UserPortNumber](string =>
      Try(string.toInt).fold(
        throwable => Left(ConfigError(throwable.getMessage)),
        int       => UserPortNumber.from(int).fold(string => Left(ConfigError(string)), Right.apply)
      )
    )

  implicit def secretDecoder: ConfigDecoder[String, Secret[String]] =
    ConfigDecoder.lift[String, Secret[String]](string => Right(Secret(string)))

  implicit def uriDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder.lift[String, Uri](string =>
      Uri.fromString(string).fold[Either[ConfigError, Uri]](
        parseFailure => Left(ConfigError(parseFailure.message)), Right.apply
      )
    )
}
