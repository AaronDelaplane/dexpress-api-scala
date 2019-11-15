package clients.steam

import cats.data.NonEmptyList
import cats.effect.{ConcurrentEffect, IO, Resource}
import cats.implicits._
import clients.steam.data._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.{Request, Uri}

import scala.concurrent.ExecutionContext.global

// @formatter:off
class SteamClient(config: SteamClientConfig, httpClient: Client[IO]) extends Http4sDsl[IO] {
  
  val logger = Slf4jLogger.getLogger[IO]
  
  def attemptToAsset(a: SteamAsset)(d: SteamDescription): IO[Asset] =
    for {
      appid            <- a.appid.fold[IO[Int]](IO.raiseError(new Exception("appid is not defined")))(IO.pure)
      assetid          <- a.assetid.fold[IO[String]](IO.raiseError(new Exception("assetid is not defined")))(IO.pure)
      classid          <- a.classid.fold[IO[String]](IO.raiseError(new Exception("classid is not defined")))(IO.pure)
      instanceid       <- a.instanceid.fold[IO[String]](IO.raiseError(new Exception("instanceid is not defined")))(IO.pure)
      tradable         <- d.tradable.fold[IO[Int]](IO.raiseError(new Exception("tradable is not defined")))(IO.pure)
      market_hash_name <- d.market_hash_name.fold[IO[String]](IO.raiseError(new Exception("market_hash_name is not defined")))(IO.pure)
      icon_url         <- d.icon_url.fold[IO[String]](IO.raiseError(new Exception("icon_url is not defined")))(IO.pure)
      asset_type       <- d.`type`.fold[IO[String]](IO.raiseError(new Exception("type is not defined")))(IO.pure)
      item_data        <- IO.pure("item_data") // Some("-") // d.market_actions // transform to `item_data`
      rarity           <- IO.pure("rarity")    // d.tags.find(_.category.map(s => s.toLowerCase === "rarity").getOrElse(false)).flatMap(_.localized_tag_name) // transform to `rarity` and `exterior`
      exterior         <- IO.pure("exterior")  // d.tags.find(_.category.map(s => s.toLowerCase === "exterior").getOrElse(false)).flatMap(_.localized_tag_name) // transform to `rarity` and `exterior`
    } yield
      Asset(appid, assetid, classid, instanceid, tradable, market_hash_name, icon_url, asset_type, exterior, rarity, item_data)
     
  def attemptCreatePairs(as: NonEmptyList[SteamAsset], ds: NonEmptyList[SteamDescription]): IO[NonEmptyList[Asset]] =
    as.traverse(a =>  
      ds
        .find(d => d.classid === a.classid && d.instanceid === a.instanceid)
        .fold[IO[Asset]](IO.raiseError(new Exception("no matching description for asset")))(attemptToAsset(a))
      )        
    
  def attemptFetchAssets(steamId: Long): IO[List[Asset]] =
    httpClient.expect[SteamInventory](
      Request[IO]()
        .withMethod(GET)
        .withUri(Uri.unsafeFromString(s"${config.steamUri}/inventory/${steamId}/730/2?l=english&count=5"))
    )
      .attempt
      .flatMap {
        case Left(throwable) => IO.raiseError(new Exception(s"request failed: $throwable"))
        case Right(value)    => {
          (value.assets.flatMap(NonEmptyList.fromList), value.descriptions.flatMap(NonEmptyList.fromList)) match {
            case (None, None) => IO.raiseError(new Exception("response missing assets & descriptions"))
            case (None, _)    => IO.raiseError(new Exception("response missing assets"))
            case (_, None)    => IO.raiseError(new Exception("response missing descriptions"))
            case (Some(assets), Some(descriptions)) => {
              (assets.isEmpty, descriptions.isEmpty) match { // todo non-empty lists at this point. not necessary
                case (true, false)  => IO.raiseError(new Exception("assets empty"))
                case (false, true)  => IO.raiseError(new Exception("descriptions empty"))
                case (true, true)   => 
                  logger.info("assets & descriptions empty") *> IO(List.empty[Asset])
                case (false, false) => {
                  logger.info("assets & descriptions non-empty") *> 
                  logger.info(s"assets size: ${assets.size}") *>
                  logger.info(s"descriptions size ${descriptions.size}")  *>
                  {
                    (assets.size, descriptions.size) match {
                      case (a, b) if a =!= b => IO.raiseError(new Exception("assets and descriptions counts not equal"))
                      case _                 => attemptCreatePairs(assets, descriptions).map(_.toList)
                    }
                  }
                }
              }
            }
          }
        }
      } 
}

object SteamClient {
  def resource(config: SteamClientConfig)(implicit CE: ConcurrentEffect[IO]): Resource[IO, SteamClient] =
    BlazeClientBuilder[IO](global).resource.map(new SteamClient(config, _))
}

