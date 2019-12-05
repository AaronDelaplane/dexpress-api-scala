package clients.csfloat

import cats.effect.{ConcurrentEffect, IO, Resource}
import datatypes.AssetDataB
import org.http4s.client.Client
import org.http4s.dsl.Http4sDsl
import org.http4s.client.blaze.BlazeClientBuilder
import java.util.UUID

import scala.concurrent.ExecutionContext.global

class CsFloatClient(config: CsFloatConfig, httpClient: Client[IO]) extends Http4sDsl[IO] {

  def getAssetDataB(assetId: String): IO[AssetDataB] = 
    IO(AssetDataB(UUID.randomUUID, 0.123))
  
}

object CsFloatClient {
  def resource(config: CsFloatConfig)(implicit CE: ConcurrentEffect[IO]): Resource[IO, CsFloatClient] =
    BlazeClientBuilder[IO](global).resource.map(new CsFloatClient(config, _))
}
