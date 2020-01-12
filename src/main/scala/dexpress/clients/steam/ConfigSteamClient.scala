package dexpress.clients.steam

import ciris.{ConfigValue, env}
import dexpress.codecs.uriDecoder
import org.http4s.Uri
import org.http4s.implicits._

final case class ConfigSteamClient(
    steamUri: Uri // todo change to just 'uri' here and in other configs
)

object ConfigSteamClient {
  val DEFAULT_STEAM_URI: Uri = uri"https://steamcommunity.com"

  val configValue: ConfigValue[ConfigSteamClient] =
    env("STEAM_URI").as[Uri].default(DEFAULT_STEAM_URI)
      .map(ConfigSteamClient.apply)
}
