package dexpress.routes

import cats.effect.IO
import dexpress.codecs._
import dexpress.enums.ResourceName.Postgres
import dexpress.functions.noneffect.{randomUUIDF, toHttpErrorResponse}
import dexpress.show._
import dexpress.types.{IdUserSteam, RequestBodyDecodingError, RequestPostUser, ResourceInvalidStateChangeError, ResourcesService, User}
import mouse.boolean._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class RoutesUser(resources: ResourcesService) extends Http4sDsl[IO] {
  
  import resources._
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "user" / "exists" :? IdUserSteamQPM(idUserSteamValidated) =>
      idUserSteamValidated
        .fold(
          parseFailures => BadRequest(parseFailures.show),
          idUserSteam   => clientPg.exists(idUserSteam).flatMap(Ok(_))
        )
        .handleErrorWith(toHttpErrorResponse)

    case GET -> Root / "user" :? IdUserSteamQPM(idUserSteamValidated) =>
      idUserSteamValidated
        .fold(
          parseFailures => BadRequest(parseFailures.show),
          idUserSteam   => clientPg.select(idUserSteam).flatMap(Ok(_))
        )
        .handleErrorWith(toHttpErrorResponse)

    case req @ POST -> Root / "user" =>
      (
        for {
          body     <- req.as[RequestPostUser].handleErrorWith(t => IO.raiseError(RequestBodyDecodingError(t)))
          exists   <- clientPg.exists(IdUserSteam(body.id_user_steam))
          _        <- exists.fold(
                        IO.raiseError(ResourceInvalidStateChangeError(Postgres, s"user with id_user_steam (${body.id_user_steam}) already exists")),
                        IO.unit
                      )
          uuid     <- randomUUIDF
          user     <- clientPg.insert(User(uuid, body.id_user_steam, body.name_first))
          response <- Ok(user)
        } yield response
      ).handleErrorWith(toHttpErrorResponse)
    
  }
  
}
