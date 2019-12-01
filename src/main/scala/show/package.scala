import cats.data.NonEmptyList
import cats.implicits._
import cats.{Monoid, Show}
import datatypes.MaybeTradableAsset

package object show {
  
  /*
  show instances -------------------------------------------------------------------------------------------------------
   */
  implicit def assetShow: Show[MaybeTradableAsset] = Show.show(x =>
    s"""
       |id:               ${x.id}
       |refresh_id:       ${x.refresh_id}
       |steam_id:         ${x.steam_id}
       |appid:            ${x.appid}
       |assetid:          ${x.assetid}
       |classid:          ${x.classid}
       |instanceid:       ${x.instanceid}
       |tradable:         ${x.tradable}
       |market_hash_name: ${x.market_hash_name}
       |icon_url:         ${x.icon_url}
       |asset_type:       ${x.asset_type}
       |exterior:         ${x.exterior}
       |rarity:           ${x.rarity}
       |link_id:          ${x.link_id}
       |sticker_info:     ${x.sticker_info}
    """.stripMargin
  )
  
  implicit def nelStringShow: Show[NonEmptyList[String]] = Show.show[NonEmptyList[String]](
    _.foldLeft("\n")(_ + _ + "\n")
  )

  implicit def parseFailureNelShow: Show[NonEmptyList[org.http4s.ParseFailure]] =
    Show.show[NonEmptyList[org.http4s.ParseFailure]](_.map(_.sanitized).show)

  implicit def parseFailureShow: Show[org.http4s.ParseFailure] =
    Show.show[org.http4s.ParseFailure](_.sanitized)

  /*
  implicit classes -----------------------------------------------------------------------------------------------------
   */
  // generic for List[A]
  implicit class ShowList[A](as: List[A])(implicit s: Show[A]) {
    def listShow: Show[List[A]] = Show.show(_.foldLeft(Monoid[String].empty)(_ + _.show))
  }
}
