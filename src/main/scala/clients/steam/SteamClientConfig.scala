package clients.steam

import cats.effect.Resource
import ciris.{ConfigValue, env}
import org.http4s.Uri
import common._

final case class SteamClientConfig(
    steamUri: Uri
)

object SteamClientConfig {
  val DEFAULT_STEAM_URI: Uri =
    Uri.unsafeFromString("https://steamcommunity.com/")

  val configValue: ConfigValue[SteamClientConfig] =
    env("STEAM_URI")
      .as[Uri]
      .default(DEFAULT_STEAM_URI)
      .map(SteamClientConfig.apply)
}
