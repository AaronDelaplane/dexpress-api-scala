package functions.noneffectful.toassets

import cats.data._
import cats.effect._
import functions.noneffectful.toassets.ToSteamAssetsAndDescriptions.toSAsAndSDsNel
import functions.noneffectful.toassets.ToSteamAssetsValidated.toSAsValidatedNel
import functions.noneffectful.toassets.ToSteamDescriptionsValidated.toSDsValidatedNel
import functions.noneffectful.toassets.ToTradablePairs.toTradablePairsNel
import types._

object ToAssets {

  def toAssets(iR: IdRefresh, iS: IdSteam, sI: SteamInventory): IO[NonEmptyList[Asset]] =
    for {
      xs     <- toSAsAndSDsNel(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      vSAs   <- toSAsValidatedNel(xs._1)
      vSDs   <- toSDsValidatedNel(xs._2)
      tPairs <- toTradablePairsNel(vSDs, vSAs)
      pairs   = tPairs.map(Asset(iS, iR))
    } yield pairs

}
