package dexpress.functions.noneffect.toassets.functions

import cats.effect.IO
import cats.implicits._
import dexpress.types._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToSteamAssetsValidated {

  private val logger = Slf4jLogger.getLogger[IO]

  def run(xs: NEL[SA]): IO[NEL[SAV]] = {
    val (errors, vSAs) = xs.toList.partitionMap(toSAValidated)
    log(errors, vSAs) *> validate(errors, vSAs).fold(
      s => IO.raiseError(DataTransformationError("steam assets", "validated steam assets", s)),
      IO.pure
    )
  }
   
  private def toSAValidated(x: SA): ErrorOr[SAV] =
    for {
      _log_id        <- s"classid=${x.classid}_instanceid=${x.instanceid}".asRight

      id_class       <- x.classid.fold(s"asset.classid not defined for (${_log_id})".asLeft[String])(_.asRight)
      id_instance    <- x.instanceid.fold(s"asset.instanceid not defined for (${_log_id})".asLeft[String])(_.asRight)
      id_app         <- x.appid.fold(s"asset.appid not defined for (${_log_id})".asLeft[Int])(_.asRight)

      id_asset_steam <- x.assetid.fold(s"asset.assetid not defined for (${_log_id})".asLeft[String])(_.asRight)
      amount         <- x.amount.fold(s"asset.instanceid not defined for (${_log_id})".asLeft[String])(_.asRight)
    } yield
      SteamAssetValidated(
        id_class       = id_class,
        id_instance    = id_instance,
        id_app         = id_app,
        id_asset_steam = id_asset_steam,
        amount         = amount
    ) 
  
  private def log(errors: List[String], xs: List[SAV]): IO[Unit] =
    logger.info(s"""
      |to-validated-steam-assets-nel-summary
      |  validated-steam-assets-count: ${xs.size}
      |  errors-count:                 ${errors.size}
      |  errors:                       $errors            
    """.stripMargin)
  
  private def validate(errors: List[String], xs: List[SAV]): ErrorOr[NEL[SAV]] =
    (errors.toNel, xs.toNel) match {
      case (Some(xs), _)     => s"the following errors occurred: ${xs.show}".asLeft[NEL[SAV]]
      case (None, None)      => "no validated steam assets were generated".asLeft[NEL[SAV]]
      case (None, Some(nel)) => nel.asRight[String]
    }  

}
