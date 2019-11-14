package steamclient

import clients.steam.Codecs._
import clients.steam.Inventory
import io.circe.Json
import io.circe.parser.parse
import org.scalatest._

import scala.io.Source

class DecoderSpec extends FlatSpec with Matchers {
  "The inventory decoder" should "decode inventory json" in {
    val source = Source.fromResource("inventory-response.json")
    val string = source.mkString
    source.close()
    parse(string)
      .getOrElse(Json.Null) // Json.Null will fail on attempt to decode
      .as[Inventory]
      .isRight should equal(true)
  }
}
