package service.functions.noneffect.toassets

import cats.data._
import cats.effect._
import service.functions.noneffect.toassets.ToSteamAssetsAndDescriptions.toSAsAndSDsNel
import service.functions.noneffect.toassets.ToSteamAssetsValidated.toSAsValidatedNel
import service.functions.noneffect.toassets.ToSteamDescriptionsValidated.toSDsValidatedNel
import service.functions.noneffect.toassets.ToTradablePairs.toTradablePairsNel
import service.types._

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
