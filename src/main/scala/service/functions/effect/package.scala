package service.functions

import io.circe.parser.parse
import io.circe.{Decoder, Json}

import scala.io.Source

package object effect {

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
