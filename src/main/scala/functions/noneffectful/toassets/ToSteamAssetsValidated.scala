package functions.noneffectful.toassets

import cats.effect.IO
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import types._

object ToSteamAssetsValidated {

  private val logger = Slf4jLogger.getLogger[IO]

  def toSAsValidatedNel(xs: NEL[SA]): IO[NEL[SAV]] = {
    val (errors, vSAs) = xs.toList.partitionMap(toSAValidated)
    for {
      _   <- log(errors, vSAs)
      nel <- validate(errors, vSAs).fold(s => IO.raiseError(new Exception(s)), IO.pure)
    } yield nel
  }
   
  private def toSAValidated(x: SA): ErrorOr[SAV] =
    for {
      _log_id    <- s"classid=${x.classid}_instanceid=${x.instanceid}".asRight

      classid    <- x.classid.fold(s"asset.classid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      instanceid <- x.instanceid.fold(s"asset.instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      appid      <- x.appid.fold(s"asset.appid-not-defined--${_log_id}".asLeft[Int])(_.asRight)

      assetid    <- x.assetid.fold(s"asset.assetid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      amount     <- x.amount.fold(s"asset.instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
    } yield
      SteamAssetValidated(
        classid    = classid,
        instanceid = instanceid,
        appid      = appid,
        assetid    = assetid,
        amount     = amount
    ) 
  
  private def log(errors: List[String], xs: List[SAV]): IO[Unit] =
    logger.info(s"""
      |to-validated-steam-assets-nel-summary
      |  validated-steam-assets-count: ${xs.size}
      |  erors-count:                  ${errors.size}
      |  errors:                       $errors            
    """.stripMargin)
  
  private def validate(errors: List[String], xs: List[SAV]): ErrorOr[NEL[SAV]] =
    (errors.toNel, xs.toNel) match {
      case (Some(_), _)      => "attempt-to-trasform-steam-assets-to-validated-steam-assets-failed".asLeft[NEL[SAV]]
      case (None, None)      => "attempt-to-transfrom-steam-assets-to-validated-steam-assets-returned-zero-results".asLeft[NEL[SAV]]
      case (None, Some(nel)) => nel.asRight[String]
    }  

}
