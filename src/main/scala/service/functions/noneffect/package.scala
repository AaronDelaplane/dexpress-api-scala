package service.functions

import cats.instances.long.catsKernelStdOrderForLong
import cats.syntax.option.catsSyntaxOptionId
import service.types.{EventRefreshAssets, IdRefresh, NEL}

package object noneffect {

  def toMaybeNonExpiredEventId(xs: NEL[EventRefreshAssets], timeNow: Long, timeExpiration: Long): Option[IdRefresh] = {
    val head = xs.sortBy(_.time).reverse.head
    if (timeNow - head.time < timeExpiration) IdRefresh(head.id_refresh).some else None
  }
  
}
