package clients.csfloat


import cats.effect.{ConcurrentEffect, IO, Resource}
import cats.implicits._
import io.circe.optics.JsonPath._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.global

class CsFloatClient(config: CsFloatConfig, httpClient: Client[IO]) extends Http4sDsl[IO] {

  def getFloatValue(assetId: String): IO[Double] =
    for {
    json       <- httpClient.expect[io.circe.Json](config.uri.+?("s", "").+?("d", "").+?("a", assetId))
    floatvalue <- root.iteminfo.floatvalue.double.getOption(json).fold[IO[Double]](
                    IO.raiseError(new Exception("missing-floatvalue-in-csfloat-response"))
                  )(
                    _.pure[IO]
                  )
    } yield floatvalue
  
}

object CsFloatClient {
  def resource(config: CsFloatConfig)(implicit CE: ConcurrentEffect[IO]): Resource[IO, CsFloatClient] =
    BlazeClientBuilder[IO](global).resource.map(new CsFloatClient(config, _))
}
