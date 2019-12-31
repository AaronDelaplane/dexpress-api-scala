package service.clients.csfloat

import ciris.{ConfigValue, env}
import org.http4s.Uri
import org.http4s.implicits._
import service.codecs.uriDecoder

final case class ConfigCsFloat(
  uri: Uri
)

object ConfigCsFloat {
  val DEFAULT_CS_FLOAT_URI = uri"https://api.csgofloat.com"
  
  val configValue: ConfigValue[ConfigCsFloat] =
    env("CS_FLOAT_URI").as[Uri].default(DEFAULT_CS_FLOAT_URI)
      .map(ConfigCsFloat.apply)
}