package functions.noneffectful

import java.util.UUID

import cats.data.NonEmptyList
import cats.instances.int.catsStdShowForInt
import cats.syntax.show._
import org.scalatest.{FlatSpec, Matchers}
import types.{EventRefreshAssets, _}

class NonEffectfulSpec extends FlatSpec with Matchers {
  
  val uuid1 = toUuid(1)
  val uuid2 = toUuid(2)
  val uuid3 = toUuid(3)
  
  def toUuid(x: Int): UUID =
    UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx".replace("x", x.show))

  val xs = NonEmptyList(
    EventRefreshAssets(uuid1, "", 10L), List(EventRefreshAssets(uuid2, "", 20L), EventRefreshAssets(uuid3, "", 30L))
  )
  
  "toMaybeNonExpiredEventId" should "return Some[IdRefresh]" in {
    toMaybeNonExpiredEventId(xs, 40L, 11L) shouldEqual Some(IdRefresh(uuid3))
  }

  "toMaybeNonExpiredEventId" should "return None" in {
    toMaybeNonExpiredEventId(xs, 40L, 9L) shouldEqual None
  }
  
}
