package routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class HealthRoutes extends Http4sDsl[IO] {
  
  def routes: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "health" => NoContent()
    }
  
}
