package functions

import scala.io.Source
import io.circe.Json
import io.circe.parser.parse
import io.circe.Decoder

package object effectful {

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
