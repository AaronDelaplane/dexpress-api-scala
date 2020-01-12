package dexpress.functions

import cats.effect.IO
import cats.implicits._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import io.circe.parser.parse
import io.circe.{Decoder, Json}
import org.http4s.dsl.Http4sDsl

import scala.io.Source

package object effect extends Http4sDsl[IO] {

  def handleError[A](externalMessage: String)(implicit l: SelfAwareStructuredLogger[IO]): IO[A] =
    l.error(externalMessage) *> IO.raiseError(new Throwable(externalMessage))
  
  def handleErrorT[A](externalMessage: String)(t: Throwable)(implicit l: SelfAwareStructuredLogger[IO]): IO[A] =
    l.error(t.getMessage) *> IO.raiseError(new RuntimeException(externalMessage))

  def getResourceUnsafe(path: String): String = {
    val source = Source.fromResource(path)
    val string = source.mkString
    source.close()
    string
  }

  def decodeFile[A](path: String)(implicit d: Decoder[A]): Decoder.Result[A] =
    parse(getResourceUnsafe(path))
      .getOrElse(Json.Null)
      .as[A]
  
}
