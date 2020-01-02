package service

import cats.implicits._
import ciris._
import eu.timepit.refined.types.net.UserPortNumber
import org.http4s.Uri
import service.codecs._
import service.types.ConfigService

object _ConfigService {
  val DEFAULT_HTTP_HOST: Uri = Uri.unsafeFromString("0.0.0.0")
  val DEFAULT_HTTP_PORT: UserPortNumber = UserPortNumber.unsafeFrom(10000)

  val configValue: ConfigValue[ConfigService] =
    (
      env("HTTP_HOST").as[Uri].default(DEFAULT_HTTP_HOST),
      env("HTTP_PORT").as[UserPortNumber].default(DEFAULT_HTTP_PORT)
    ).parMapN(ConfigService)
}