package dexpress

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import dexpress.routes._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.syntax.kleisli._


object Main extends IOApp with Http4sDsl[IO] {
  
  override def run(args: List[String]): IO[ExitCode] =
    _ResourcesService.make.use(
      resources =>
        for {
          _ <- resources.clientPg.verifyConnection
          _ <- resources.clientFlyway.migrate
          _ <- BlazeServerBuilder[IO]
                 .bindHttp(
                   resources.configService.httpPort.value,
                   resources.configService.httpHost.renderString
                 )
                 .withHttpApp {
       
                   val routesHealth = new RoutesHealth
                   val routesAssets = new RoutesAssets(resources)
                   val routesUser   = new RoutesUser(resources)
       
                   val routes: HttpRoutes[IO] = Router[IO](
                     "/" -> {
                       routesHealth.routes <+> routesAssets.routes <+> routesUser.routes
                     }
                   )
       
                   Logger.httpApp(logHeaders = false, logBody = false)(routes.orNotFound)
                 }
                 .serve
                 .compile
                 .drain
                 .as(ExitCode.Success)
        } yield ()
      )
      .as(ExitCode.Success)
}
