package types

import java.util.UUID

import cats.Monoid
import cats.instances.string._
import dexpress.functions.noneffect.randomUUIDF
import dexpress.types._
import org.scalatest.{FlatSpec, Matchers}

class TypesSpec extends FlatSpec with Matchers {

  val uuid = randomUUIDF.unsafeRunSync
  val iR   = IdRefresh(uuid)
  val iU   = IdUser(uuid)
  val iUS  = IdUserSteam(Monoid[String].empty)
  
  "asset constructor" should "create unique id_asset" in {
    List.fill(3)(
      Asset(iU, iUS, IdRefresh(UUID.randomUUID))((SteamDescriptionValidated.empty, SteamAssetValidated.empty))
    ).map(_.id_asset).distinct.size shouldEqual(3)
  }
  
}
