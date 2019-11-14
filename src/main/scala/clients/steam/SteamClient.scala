package clients.steam

import cats.effect.{ConcurrentEffect, IO, Resource}
import io.circe.Json
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Uri}

import scala.concurrent.ExecutionContext.global

case class SteamAsset(
    appid: Int,
    assetid: String,
    classid: String,
    instanceid: String,
    tradable: Int, // 0 or 1
    market_hash_name: String,
    icon_url: String,
    item_data: String,
    `type`: String,
    rarity: String,
    exterior: String
)

class SteamClient(config: SteamClientConfig, httpClient: Client[IO])
    extends Http4sDsl[IO] {
  def getSteamInventory(steamId: Long): IO[Inventory] =
    httpClient.expect[Inventory](
      Request[IO]()
        .withMethod(GET)
        .withUri(
          Uri.unsafeFromString(
            s"${config.steamUri}/inventory/${steamId}/730/2?l=english&count=5"
          )
        )
    )

  def parseSteamAssetInventoryJson(json: Json): IO[List[_]] = ???
}

object SteamClient {
  def resource(
      config: SteamClientConfig
  )(implicit CE: ConcurrentEffect[IO]): Resource[IO, SteamClient] =
    BlazeClientBuilder[IO](global).resource.map(new SteamClient(config, _))
}
