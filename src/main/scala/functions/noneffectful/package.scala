package functions

import types.{EventRefreshAssets, IdRefresh, NEL}
import cats.syntax.option.catsSyntaxOptionId
import cats.instances.long.catsKernelStdOrderForLong

package object noneffectful {

  def toMaybeNonExpiredEventId(xs: NEL[EventRefreshAssets], timeNow: Long, timeExpiration: Long): Option[IdRefresh] = {
    val head = xs.sortBy(_.time).reverse.head
    if (timeNow - head.time < timeExpiration) IdRefresh(head.id_refresh).some else None
  }
  
}
