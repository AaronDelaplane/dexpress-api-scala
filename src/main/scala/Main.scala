import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._
import routes.{HealthRoutes, InventoryRoutes}

// @formatter:off
object Main extends IOApp with Http4sDsl[IO] {
  
  override def run(args: List[String]): IO[ExitCode] =
    ServiceResources.make.use(resources =>
      resources.flywayClient.migrate *>
      BlazeServerBuilder[IO]
        .bindHttp(
          resources.serviceConfig.httpPort.value,
          resources.serviceConfig.httpHost.renderString
        )
        .withHttpApp {
          
          val healthRoutes    = new HealthRoutes
          val inventoryRoutes = new InventoryRoutes(resources.sqlClient, resources.steamClient)
          
          val routes: HttpRoutes[IO] = Router[IO](
            "/" -> {
              healthRoutes.routes <+> inventoryRoutes.routes 
            }
          )
          
          Logger.httpApp(logHeaders = false, logBody = false)(routes.orNotFound)
        }
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
      )
      .as(ExitCode.Success)
}
