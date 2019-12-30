package functions.noneffectful

import cats.data.NonEmptyList
import org.scalatest.{FlatSpec, Matchers}
import types.EventRefreshAssets
import java.util.UUID
import cats.syntax.show._
import cats.instances.int.catsStdShowForInt

class NonEffectfulSpec extends FlatSpec with Matchers {
  
  val uuid1 = toUuid(1)
  val uuid2 = toUuid(2)
  val uuid3 = toUuid(3)
  
  def toUuid(x: Int): UUID =
    UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx".replace("x", x.show))

  val xs = NonEmptyList(
    EventRefreshAssets(uuid1, "", 10L), List(EventRefreshAssets(uuid2, "", 20L), EventRefreshAssets(uuid3, "", 30L))
  )
  
  "toMaybeNonExpiredEventId" should "return non-expired event id" in {
    toMaybeNonExpiredEventId(xs, 40L, 11L) shouldEqual Some(uuid3)
  }

  "toMaybeNonExpiredEventId" should "return non-expired event id" in {
    toMaybeNonExpiredEventId(xs, 40L, 9L) shouldEqual None
  }
  
}
