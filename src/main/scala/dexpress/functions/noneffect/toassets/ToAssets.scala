package dexpress.functions.noneffect.toassets

import cats.effect._
import cats.instances.string.catsKernelStdOrderForString
import cats.syntax.eq._
import dexpress.functions.noneffect.toassets.functions._
import dexpress.types._

object ToAssets {

  def run(iR: IdRefresh, iS: IdSteam, sI: SteamInventory): IO[NEL[Asset]] =
    for {
      xs     <- ToSteamAssetsAndDescriptions.run(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      sAsV   <- ToSteamAssetsValidated.run(xs._1)
      sDsV   <- ToSteamDescriptionsValidated.run(xs._2)
      tPairs <- ToTradablePairs.run(sDsV, sAsV)
      assets  = tPairs.map(Asset(iS, iR)(_)) // must pass placeholder else map will cache expression including 'id_asset'
    } yield assets

  def run(iR: IdRefresh, iS: IdSteam, sI: SteamInventory, assetsA: NEL[Asset]): IO[NEL[Asset]] =
    for {
      xs     <- ToSteamAssetsAndDescriptions.run(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      sAsV   <- ToSteamAssetsValidated.run(xs._1)
      sDsV   <- ToSteamDescriptionsValidated.run(xs._2)
      tPairs <- ToTradablePairs.run(sDsV, sAsV)
      assets  = tPairs.map(
                  t => {
                    assetsA.find(_.assetid === t._2.assetid).fold(
                      Asset(iS, iR)(t)
                    )(
                      assetA => 
                        Asset(iS, iR, assetA.id_asset)(t)
                          .copy(trading = assetA.trading)
                    )
                  }
                )
    } yield assets

}
