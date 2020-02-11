package dexpress.clients.csfloat

import cats.effect.{ConcurrentEffect, IO, Resource}
import cats.implicits._
import dexpress.functions.effect._
import dexpress.types.IdAssetSteam
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.optics.JsonPath._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.global

class ClientCsFloat(config: ConfigCsFloat, clientHttp: Client[IO]) extends Http4sDsl[IO] {

  implicit private val logger = Slf4jLogger.getLogger[IO]
  
  def toFloatValue(iAS: IdAssetSteam): IO[Double] = scala.util.Random.nextDouble.pure[IO]
//    for {
//      json <- clientHttp
//                .expect[io.circe.Json](config.uri.+?("s", "").+?("d", "").+?("a", iAS.value))
//                .handleErrorWith(handleErrorT(s"csfloat call failed for assetid (${iAS.value})"))
//      fV   <- root.iteminfo.floatvalue.double.getOption(json)
//                .fold[IO[Double]](handleError("csfloat response missing floatvalue"))(_.pure[IO])
//    } yield fV
  
}

object ClientCsFloat {
  def resource(config: ConfigCsFloat)(implicit CE: ConcurrentEffect[IO]): Resource[IO, ClientCsFloat] =
    BlazeClientBuilder[IO](global).resource.map(new ClientCsFloat(config, _))
}
