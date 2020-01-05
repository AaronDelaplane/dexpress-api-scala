package service.clients.csfloat

import cats.effect.{ConcurrentEffect, IO, Resource}
import cats.implicits._
import io.circe.optics.JsonPath._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.global

class ClientCsFloat(config: ConfigCsFloat, clientHttp: Client[IO]) extends Http4sDsl[IO] {

  def toFloatValue(assetId: String): IO[Double] =
    for {
      json       <- clientHttp
                      .expect[io.circe.Json](config.uri.+?("s", "").+?("d", "").+?("a", assetId))
                      .attempt
                      .flatMap(_.fold(_ => IO.raiseError(new Exception(s"csfloat-call-failed")), _.pure[IO]))
      floatvalue <- root.iteminfo.floatvalue.double.getOption(json).fold[IO[Double]](
                      IO.raiseError(new Exception("missing-floatvalue-in-csfloat-response"))
                    )(
                      _.pure[IO]
                    )
    } yield floatvalue
  
}

object ClientCsFloat {
  def resource(config: ConfigCsFloat)(implicit CE: ConcurrentEffect[IO]): Resource[IO, ClientCsFloat] =
    BlazeClientBuilder[IO](global).resource.map(new ClientCsFloat(config, _))
}
