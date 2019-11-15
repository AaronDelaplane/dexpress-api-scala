package steamclient

import clients.steam.data._
import io.circe.Json
import io.circe.parser.parse
import io.circe.syntax._
import org.scalatest._
import steamclient.DecoderSpec._

import scala.io.Source

/*
Notes:

Json.Null will fail on attempt to decode
 */

// @formatter:off
class DecoderSpec extends FlatSpec with Matchers {

  // todo abstract tests into common fn
  
  "steam asset decoder" should "decode json" in {
    parse(
      getResourceUnsafe("steam-asset.json")
    )
      .getOrElse(Json.Null)
      .as[SteamAsset]
      .isRight should equal(true)
  }
  
  "steam description decoder" should "decode json" in {
    parse(
      getResourceUnsafe("steam-description.json")
    )
      .getOrElse(Json.Null)
      .as[SteamDescription]
      .isRight should equal(true)
  }
  
  "steam tag decoder" should "decode json" in {
    parse(
      getResourceUnsafe("steam-tag.json")
    )
      .getOrElse(Json.Null)
      .as[SteamTag]
      .isRight should equal(true)
  }  
  
  "steam market action decoder" should "decode json" in {
    parse(
      getResourceUnsafe("steam-market-action.json")
    )
      .getOrElse(Json.Null)
      .as[SteamMarketAction]
      .isRight should equal(true)
  }
  
  "steam inventory decoder" should "decode empty json" in {
    parse(
      getResourceUnsafe("steam-inventory-empty.json")
    )
      .getOrElse(Json.Null)
      .as[SteamInventory]
      .isRight should equal(true)
  }
  
  "steam inventory decoder" should "decode non-empty json" in {
    parse(
      getResourceUnsafe("steam-inventory-nonempty.json")
    )
    .getOrElse(Json.Null) // Json.Null will fail on attempt to decode
    .as[SteamInventory]
    .map(_.asJson)
    .isRight should equal(true)
  }
}

object DecoderSpec {
  def getResourceUnsafe(path: String): String = {
    val source = Source.fromResource(path)
    val string = source.mkString
    source.close()
    string
  }
    
}
