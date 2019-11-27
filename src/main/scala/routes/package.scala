import cats.effect.IO
import enumeratum._
import org.http4s.{ParseFailure, QueryParamDecoder}
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import cats.kernel.Monoid

package object routes extends Http4sDsl[IO] {
  /*
  helpers --------------------------------------------------------------------------------------------------------------
   */
  def toParseFailureMessage[A](name: String, values: Seq[A]): String =
    s"$name is not one of ${values}"

  /*
  enums ----------------------------------------------------------------------------------------------------------------
   */
  sealed trait StateTo extends EnumEntry
  val stateto = "stateto"
  object StateTo extends Enum[StateTo] {
    val values = findValues
    case object trading    extends StateTo
    case object nottrading extends StateTo
  }

  /*
  query param matchers -------------------------------------------------------------------------------------------------
   */
  implicit val stateToQueryParamDecoder: QueryParamDecoder[StateTo] = 
    QueryParamDecoder[String].emap[StateTo](s => 
      StateTo.withNameLowercaseOnlyOption(s)
        .fold(
          ParseFailure(toParseFailureMessage(stateto, StateTo.values), Monoid[String].empty).asLeft[StateTo]
        )(
          _.asRight[ParseFailure])
        )
  
  object StateToQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[StateTo]("stateto")

}
