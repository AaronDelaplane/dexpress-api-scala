package service.functions.noneffect.toassets

import cats.data._
import cats.effect._
import service.types._

object ToAssets {

  def toAssets(iR: IdRefresh, iS: IdSteam, sI: SteamInventory): IO[NonEmptyList[Asset]] =
    for {
      xs     <- ToSteamAssetsAndDescriptions.run(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      sAsV   <- ToSteamAssetsValidated.run(xs._1)
      sDsV   <- ToSteamDescriptionsValidated.run(xs._2)
      tPairs <- ToTradablePairs.run(sDsV, sAsV)
    } yield tPairs.map(Asset(iS, iR))

}
