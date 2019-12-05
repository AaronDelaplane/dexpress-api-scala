package clients.steam

import ciris.{ConfigValue, env}
import codecs.uriDecoder
import org.http4s.Uri

final case class SteamClientConfig(
    steamUri: Uri // todo change to just 'uri' here and in other configs
)

object SteamClientConfig {
  val DEFAULT_STEAM_URI: Uri = Uri.unsafeFromString("https://steamcommunity.com")

  val configValue: ConfigValue[SteamClientConfig] =
    env("STEAM_URI").as[Uri].default(DEFAULT_STEAM_URI)
      .map(SteamClientConfig.apply)
}
