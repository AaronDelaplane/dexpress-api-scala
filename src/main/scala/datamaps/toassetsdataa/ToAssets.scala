package datamaps.toassetsdataa
import java.util.UUID

import cats.data._
import cats.effect._
import datamaps.toassetsdataa.helpers.ToSteamAssetsAndDescriptions.toSAsAndSDsNel
import datamaps.toassetsdataa.helpers.ToTradablePairs.toTradablePairsNel
import datamaps.toassetsdataa.helpers.ToValidatedSteamAssets.toValidatedSAsNel
import datamaps.toassetsdataa.helpers.ToValidatedSteamDescriptions.toValidatedSDsNel
import datatypes._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToAssets {
  
  private val logger = Slf4jLogger.getLogger[IO]

  def toAssets(refreshId: UUID, steamId: String, sI: SteamInventory): IO[NonEmptyList[Asset]] =
    for {
      xs     <- toSAsAndSDsNel(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      vSAs   <- toValidatedSAsNel(xs._1)
      vSDs   <- toValidatedSDsNel(xs._2)
      tPairs <- toTradablePairsNel(vSDs, vSAs)
      pairs   = tPairs.map(Asset(steamId, refreshId))
    } yield pairs

}
