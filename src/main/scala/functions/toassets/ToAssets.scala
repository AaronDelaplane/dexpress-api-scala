package functions.toassets

import java.util.UUID

import cats.data._
import cats.effect._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import types._
import functions.ToSteamAssetsAndDescriptions.toSAsAndSDsNel
import functions.ToSteamAssetsValidated.toSAsValidatedNel
import functions.ToSteamDescriptionsValidated.toSDsValidatedNel
import functions.ToTradablePairs.toTradablePairsNel

object ToAssets {
  
  private val logger = Slf4jLogger.getLogger[IO]

  def toAssets(refreshId: UUID, steamId: String, sI: SteamInventory): IO[NonEmptyList[Asset]] =
    for {
      xs     <- toSAsAndSDsNel(sI).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      vSAs   <- toSAsValidatedNel(xs._1)
      vSDs   <- toSDsValidatedNel(xs._2)
      tPairs <- toTradablePairsNel(vSDs, vSAs)
      pairs   = tPairs.map(Asset(steamId, refreshId))
    } yield pairs

}
