package types

import dexpress.types._
import java.util.UUID

import org.scalatest.{FlatSpec, Matchers}

class TypesSpec extends FlatSpec with Matchers {
  
  "asset constructor" should "create unique id_asset" in {
    List.fill(3)(
      Asset(IdSteam(""), IdRefresh(UUID.randomUUID))((SteamDescriptionValidated.empty, SteamAssetValidated.empty))
    ).map(_.id_asset).distinct.size shouldEqual(3)
  }
  
}
