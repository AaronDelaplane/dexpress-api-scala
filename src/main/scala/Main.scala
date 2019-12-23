import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._
import routes.{RoutesHealth, RoutesInventory}

object Main extends IOApp with Http4sDsl[IO] {
  
  override def run(args: List[String]): IO[ExitCode] =
    ResourcesService.make.use(resources =>
      resources.clientFlyway.migrate *>
      BlazeServerBuilder[IO]
        .bindHttp(
          resources.configService.httpPort.value,
          resources.configService.httpHost.renderString
        )
        .withHttpApp {
          
          val routesHealth    = new RoutesHealth
          val routesInventory = new RoutesInventory(resources.clientPg, resources.clientSteam, resources.clientCsgoFloat)
          
          val routes: HttpRoutes[IO] = Router[IO](
            "/" -> {
              routesHealth.routes <+> routesInventory.routes 
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
