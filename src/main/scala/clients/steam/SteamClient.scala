package clients.steam

import cats.effect.{ConcurrentEffect, IO, Resource}
import common.SteamInventory
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Uri}

import scala.concurrent.ExecutionContext.global

class SteamClient(config: SteamClientConfig, httpClient: Client[IO]) extends Http4sDsl[IO] {
  
  val log = Slf4jLogger.getLogger[IO]
  
  def getInventory(steamId: String, count: Int): IO[SteamInventory] =
    for {
      steamInventory <- httpClient.expect[SteamInventory](
                          Request[IO]()
                            .withMethod(GET)
                            .withUri(Uri.unsafeFromString(s"${config.steamUri}/inventory/$steamId/730/2?l=english&count=$count"))
                          )
      _              <- log.info(s"""
                          |steam-inventory-fetch-results:
                          |  assets-count:       ${steamInventory.assets.map(_.size)}
                          |  descriptions-count: ${steamInventory.descriptions.map(_.size)}
                          |  unique-assetids:    ${steamInventory.assets.map(_.map(_.assetid).distinct.size).get}
                          |  unique-classids:    ${steamInventory.assets.map(_.map(_.classid).distinct.size).get}
                          |  unique-instanceids: ${steamInventory.assets.map(_.map(_.instanceid).distinct.size).get}
                          |""".stripMargin)

    } yield steamInventory
  
}

object SteamClient {
  def resource(config: SteamClientConfig)(implicit CE: ConcurrentEffect[IO]): Resource[IO, SteamClient] =
    BlazeClientBuilder[IO](global).resource.map(new SteamClient(config, _))
}
