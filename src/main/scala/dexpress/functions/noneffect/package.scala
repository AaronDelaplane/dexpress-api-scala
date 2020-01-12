package dexpress.functions

import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.IO
import dexpress.types._
import org.http4s.dsl.io._
import org.http4s.headers.`WWW-Authenticate`
import org.http4s.{Challenge, Response}

// todo these functions should be in effect
package object noneffect {
  
  def randomUUIDF: IO[UUID] = IO.delay(java.util.UUID.randomUUID)
  
  def toHttpErrorResponse(t: Throwable): IO[Response[IO]] =
    t match {
      case sE: ServiceError => toHttpServiceErrorResponse(sE)
      /*
      This should never occur. If it does, update code to return the appropriate ServiceError. 
      
      The reason this may occur is because calls to `IO.raiseError(_)` are used within functions that have a codomain
      of `IO[A]` rather than `IO[Either[ServiceError, A]]` and thus the `Throwable` is not subtyped to `ServiceError`
      
      This is done this way to bc it is simpler, though it is not ideal from a type restriction perspective. It can
      be revisited.  
       */
      case t: Throwable => InternalServerError(s"api correction needed. unaccounted for error state occurred (${t.getMessage}")
    }
  
  // this function exists to ensure that all instances of `ServiceError` are mapped to an appropriate `Response`
  def toHttpServiceErrorResponse(sE: ServiceError): IO[Response[IO]] =
    sE match {
      case e: DataTransformationError           => InternalServerError(e.message)
      case e: RequestBodyDecodingError          => BadRequest(e.message)  
      case e: ResourceAuthenticationError       => Unauthorized(`WWW-Authenticate`.apply(NonEmptyList(Challenge("scheme", "realm"), Nil)), e.message)
      case e: ResourceDuplicationError          => Conflict(e.message)
      case e: ResourceGenericError              => InternalServerError(e.message)
      case e: ResourceInvalidStateChangeError => Conflict(e.message)
      case e: ResourceNotFoundError             => NotFound(e.message)
      case e: ResourceTransactionError          => InternalServerError(e.message)
      case e                                    => InternalServerError(s"api must be updated. unaccounted for service error has occurred: ${e.message}")  
    }
}
