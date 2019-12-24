//import DecoderSpec._
import codecs._
import datatypes._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json}
import org.scalatest._
import utils._

import scala.io.Source

/*
Notes:

Json.Null will fail on attempt to decode
 */
class DecoderSpec extends FlatSpec with Matchers {
  
  def attemptDecode[A](path: String)(implicit d: Decoder[A]): Assertion =
    decodeFile[A](path).isRight should equal(true)
  
  def attemptDecodeEncode[A](path: String)(implicit d: Decoder[A], e: Encoder[A]): Assertion =
    decodeFile[A](path).map(_.asJson).isRight should equal(true)
  
  "steam asset decoder" should "decode json" in
    attemptDecode[SteamAsset]("steam-asset.json")
  
  "steam description decoder" should "decode json" in
    attemptDecode[SteamDescription]("steam-description.json")

  "steam tag decoder" should "decode json" in
    attemptDecode[SteamTag]("steam-tag.json")

  "steam market action decoder" should "decode json" in
    attemptDecode[SteamMarketAction]("steam-market-action.json")

  "steam inventory decoder" should "decode empty json" in
    attemptDecode[SteamInventory]("steam-inventory-empty.json")

  "steam inventory codecs" should "decode & encode json" in
    attemptDecodeEncode[SteamInventory]("steam-inventory.json")

  "steam inventory codecs" should "decode & encode large json" in
    attemptDecodeEncode[SteamInventory]("steam-inventory-large.json")            
   
}

object DecoderSpec {
  
//  def getResourceUnsafe(path: String): String = {
//    val source = Source.fromResource(path)
//    val string = source.mkString
//    source.close()
//    string
//  }
//
//  def decodeFile[A](path: String)(implicit d: Decoder[A]): Decoder.Result[A] =
//    parse(getResourceUnsafe(path))
//      .getOrElse(Json.Null)
//      .as[A]
    
}
