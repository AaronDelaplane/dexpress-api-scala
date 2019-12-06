package datamaps.toassetsdataa
import java.util.UUID

import cats.data._
import cats.effect._
import datamaps.toassetsdataa.helpers.ToAssetsAndDescriptions.toAssetsAndDescriptionsNel
import datamaps.toassetsdataa.helpers.ToTradablePairs.toTradablePairsNel
import datamaps.toassetsdataa.helpers.ToValidatedSteamAssets.toValidatedSAsNel
import datamaps.toassetsdataa.helpers.ToValidatedSteamDescriptions.toValidatedSDsNel
import datatypes._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToAssetsDataA {
  
  private val logger = Slf4jLogger.getLogger[IO]

  def toAssetsDataA(refreshId: UUID, steamId: String, sI: SteamInventory): IO[NonEmptyList[AssetDataA]] =
    for {
      xs     <- toAssetsAndDescriptionsNel(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      vSAs   <- toValidatedSAsNel(xs._1)
      vSDs   <- toValidatedSDsNel(xs._2)
      tPairs <- toTradablePairsNel(vSDs, vSAs)
      pairs   = tPairs.map(AssetDataA(steamId, refreshId))
    } yield pairs

}
