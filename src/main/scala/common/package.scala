import cats.implicits._
import ciris.{ConfigDecoder, ConfigError, Secret}
import eu.timepit.refined.types.net.UserPortNumber
import org.http4s.Uri

import scala.util.Try

package object common {
  implicit def portDecoder: ConfigDecoder[String, UserPortNumber] =
    ConfigDecoder.lift[String, UserPortNumber](
      string =>
        Try(string.toInt).fold(
          throwable => Left(ConfigError(throwable.getMessage)),
          int =>
            UserPortNumber
              .from(int)
              .fold(string => Left(ConfigError(string)), Right.apply)
        )
    )

  implicit def secretDecoder: ConfigDecoder[String, Secret[String]] =
    ConfigDecoder.lift[String, Secret[String]](string => Right(Secret(string)))

  implicit def uriDecoder: ConfigDecoder[String, Uri] =
    ConfigDecoder.lift[String, Uri](
      string =>
        Uri
          .fromString(string)
          .fold[Either[ConfigError, Uri]](
            parseFailure => Left(ConfigError(parseFailure.message)),
            Right.apply
          )
    )
}
