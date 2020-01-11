package dexpress.functions.noneffect.toassets.functions

import cats.data.NonEmptyList
import cats.implicits._
import dexpress.types._

object ToSteamAssetsAndDescriptions {

  def run(si: SI): ErrorOr[(NEL[SA], NEL[SD])] = {
      type R = (NEL[SA], NEL[SD])
      (si.assets, si.descriptions) match {
        case (None, None) => "missing-assets-&-descriptions".asLeft[R]
        case (None, _)    => "missing-assets".asLeft[R]
        case (_, None)    => "missing-descriptions".asLeft[R]
        case (Some(assets), Some(descriptions)) => {
          (NonEmptyList.fromList(assets), NonEmptyList.fromList(descriptions)) match {
            case (None, None)         => "assets-&-descriptions-empty".asLeft[R]
            case (None, Some(_))      => "assets-empty-descriptions-non-empty".asLeft[R]
            case (Some(_), None)      => "descriptions-empty-assets-non-empty".asLeft[R]
            case (Some(as), Some(ds)) => ((as, ds)).asRight[String]
          }
        }
      }
    }
}
