package clients.steam

import cats.effect.{ConcurrentEffect, IO, Resource}
import functions_io.decodeFile
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import types.SteamInventory
import codecs.steamInventoryDecoder

import scala.concurrent.ExecutionContext.global

class ClientSteam(config: ConfigSteamClient, httpClient: Client[IO]) extends Http4sDsl[IO] {
  
  private val logger = Slf4jLogger.getLogger[IO]
  
  def getInventory(steamId: String, count: Int): IO[SteamInventory] =
    decodeFile[SteamInventory]("steam-inventory-large.json").fold(x => IO.raiseError(new Exception(x)), IO.pure)

  //  def getInventory(steamId: String, count: Int): IO[SteamInventory] =
//    for {
//      si <- httpClient.expect[SteamInventory](
//              Request[IO]()
//                .withMethod(GET)
//                .withUri(Uri.unsafeFromString(s"${config.steamUri}/inventory/$steamId/730/2?l=english&count=$count")))
//      _  <- logger.info(s"""
//              |steam-inventory-fetch-results:
//              |  assets-count:       ${si.assets.map(_.size)}
//              |  descriptions-count: ${si.descriptions.map(_.size)}
//              |  unique-assetids:    ${si.assets.map(_.map(_.assetid).distinct.size).get}
//              |  unique-classids:    ${si.assets.map(_.map(_.classid).distinct.size).get}
//              |  unique-instanceids: ${si.assets.map(_.map(_.instanceid).distinct.size).get}
//              |""".stripMargin)
//
//    } yield si
  
}

object ClientSteam {
  def resource(config: ConfigSteamClient)(implicit CE: ConcurrentEffect[IO]): Resource[IO, ClientSteam] =
    BlazeClientBuilder[IO](global).resource.map(new ClientSteam(config, _))
}
