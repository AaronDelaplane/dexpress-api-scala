import java.util.UUID

import cats.effect.IO
import enumeratum._
import org.http4s.{ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import cats.kernel.Monoid
import java.util.UUID

import scala.util.matching.Regex

package object routes extends Http4sDsl[IO] {
  /*
  helpers --------------------------------------------------------------------------------------------------------------
   */
  def toParseFailureMessage[A](name: String, values: Seq[A]): String =
    s"$name is not one of ${values}"
  
  def toParseFailureMessage[A](name: String, value: A): String =
    s"$name is not valid: $value"

  /*
  enums ----------------------------------------------------------------------------------------------------------------
   */
  sealed trait StateTo extends EnumEntry
  object StateTo extends Enum[StateTo] {
    val values = findValues
    case object trading    extends StateTo
    case object nottrading extends StateTo
  }

  /*
  query param matchers -------------------------------------------------------------------------------------------------
   */
  val uuidRegex = new Regex(".*[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}")
  implicit def uuidQPD: QueryParamDecoder[UUID] =
    QueryParamDecoder[String].emap[UUID](s =>
      uuidRegex.findFirstIn(s).fold(
        ParseFailure(toParseFailureMessage("uuid", s), Monoid[String].empty).asLeft[UUID]
      )(
        UUID.fromString(_).asRight[ParseFailure]
      )
    )
  object UuidToQPM extends ValidatingQueryParamDecoderMatcher[UUID]("uuid")
  
  implicit def stateToQPD: QueryParamDecoder[StateTo] = 
    QueryParamDecoder[String].emap[StateTo](s => 
      StateTo.withNameLowercaseOnlyOption(s)
        .fold(
          ParseFailure(toParseFailureMessage("stateto", StateTo.values), Monoid[String].empty).asLeft[StateTo]
        )(
          _.asRight[ParseFailure])
        )
  object StateToQPM extends ValidatingQueryParamDecoderMatcher[StateTo]("stateto")

}
