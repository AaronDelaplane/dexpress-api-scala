package dexpress.clients.steam

import cats.effect.{ConcurrentEffect, IO, Resource}
import dexpress.codecs._
import dexpress.enums.ResourceName.Steam
import dexpress.types.{SteamInventory, _}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Uri}

import scala.concurrent.ExecutionContext.global

class ClientSteam(config: ConfigSteamClient, httpClient: Client[IO]) extends Http4sDsl[IO] {
  
  private val logger = Slf4jLogger.getLogger[IO]
  
  def getInventory(iS: IdUserSteam): IO[SteamInventory] = {
    val appId = 730
    val contextId = 2
    for {
      si <- httpClient.expectOr[SteamInventory](
              Request[IO]()
                .withMethod(GET)
                .withUri(Uri.unsafeFromString(s"${config.steamUri}/inventory/${iS.value}/$appId/$contextId?l=english&count=1000"))
            )(
              onError => onError.status match {
                case Forbidden => IO.raiseError(ResourceAuthenticationError(Steam, iS.value))
                case _         => IO.raiseError(ResourceGenericError(Steam, "read inventory"))
              }
            )
      _  <- logger.info(s"""
              |steam-inventory-fetch-results:
              |  assets-count:       ${si.assets.map(_.size)}
              |  descriptions-count: ${si.descriptions.map(_.size)}
              |  unique-assetids:    ${si.assets.map(_.map(_.assetid).distinct.size).get}
              |  unique-classids:    ${si.assets.map(_.map(_.classid).distinct.size).get}
              |  unique-instanceids: ${si.assets.map(_.map(_.instanceid).distinct.size).get}
              |""".stripMargin)

    } yield si
  }

  /*
  import functions_io.decodeFile
  
  def getInventory(steamId: String, count: Int): IO[SteamInventory] =
    decodeFile[SteamInventory]("steam-inventory-large.json").fold(x => IO.raiseError(new Exception(x)), IO.pure)
  */
  
}

object ClientSteam {
  def resource(config: ConfigSteamClient)(implicit CE: ConcurrentEffect[IO]): Resource[IO, ClientSteam] =
    BlazeClientBuilder[IO](global).resource.map(new ClientSteam(config, _))
}
