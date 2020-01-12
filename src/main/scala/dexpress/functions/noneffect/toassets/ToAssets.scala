package dexpress.functions.noneffect.toassets

import cats.effect._
import cats.instances.string.catsKernelStdOrderForString
import cats.syntax.eq._
import dexpress.functions.noneffect.toassets.functions._
import dexpress.types._

object ToAssets {

  def run(iR: IdRefresh, iU: IdUser, iS: IdUserSteam, sI: SteamInventory): IO[NEL[Asset]] =
    for {
      xs     <- ToSteamAssetsAndDescriptions.run(sI).fold(
                  s => IO.raiseError(DataTransformationError("steam inventory", "assets and descriptions", s)),
                  IO.pure
                )
      sAsV   <- ToSteamAssetsValidated.run(xs._1)
      sDsV   <- ToSteamDescriptionsValidated.run(xs._2)
      tPairs <- ToTradablePairs.run(sDsV, sAsV)
      assets  = tPairs.map(Asset(iU, iS, iR)(_)) // must pass placeholder else map will cache expression including 'id_asset'
    } yield assets

  def   runCombine(iR: IdRefresh, iU: IdUser, iUS: IdUserSteam, sI: SteamInventory, assetsA: List[Asset]): IO[NEL[Asset]] =
    for {
      xs     <- ToSteamAssetsAndDescriptions.run(sI).fold(
                  s => IO.raiseError(DataTransformationError("steam inventory", "assets and descriptions", s)),
                  IO.pure
                )
      sAsV   <- ToSteamAssetsValidated.run(xs._1)
      sDsV   <- ToSteamDescriptionsValidated.run(xs._2)
      tPairs <- ToTradablePairs.run(sDsV, sAsV)
      assets  = tPairs.map(t => 
                  assetsA
                    .find(_.id_asset_steam === t._2.id_asset_steam)
                    .fold(
                      Asset(iU, iUS, iR)(t)
                    )(
                      assetA => Asset(iU, iUS, iR, assetA.id_asset)(t).copy(is_trading = assetA.is_trading)
                    )
                  )
    } yield assets

}
