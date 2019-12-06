package datamaps.toassetsdataa.helpers

import cats.effect.IO
import cats.implicits._
import datatypes._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToValidatedSteamAssets {

  private val logger = Slf4jLogger.getLogger[IO]

  def toValidatedSAsNel(xs: NEL[SA]): IO[NEL[VSA]] = {
    val (errors, vSAs) = xs.toList.partitionMap(toValidatedSA)
    for {
      _   <- log(errors, vSAs)
      nel <- validate(errors, vSAs).fold(s => IO.raiseError(new Exception(s)), IO.pure)
    } yield nel
  }
   
  private def toValidatedSA(x: SA): ErrorOr[VSA] =
    for {
      _log_id    <- s"classid=${x.classid}_instanceid=${x.instanceid}".asRight

      classid    <- x.classid.fold(s"asset.classid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      instanceid <- x.instanceid.fold(s"asset.instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      appid      <- x.appid.fold(s"asset.appid-not-defined--${_log_id}".asLeft[Int])(_.asRight)

      assetid    <- x.assetid.fold(s"asset.assetid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      amount     <- x.amount.fold(s"asset.instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
    } yield
      ValidatedSteamAsset(
        classid    = classid,
        instanceid = instanceid,
        appid      = appid,
        assetid    = assetid,
        amount     = amount
    ) 
  
  private def log(errors: List[String], xs: List[VSA]): IO[Unit] =
    logger.info(s"""
      |to-validated-steam-assets-nel-summary
      |  validated-steam-assets-count: ${xs.size}
      |  erors-count:                  ${errors.size}
      |  errors:                       $errors            
    """.stripMargin)
  
  private def validate(errors: List[String], xs: List[VSA]): ErrorOr[NEL[VSA]] =
    (errors.toNel, xs.toNel) match {
      case (Some(_), _)      => "attempt-to-trasform-steam-assets-to-validated-steam-assets-failed".asLeft[NEL[VSA]]
      case (None, None)      => "attempt-to-transfrom-steam-assets-to-validated-steam-assets-returned-zero-results".asLeft[NEL[VSA]]
      case (None, Some(nel)) => nel.asRight[String]
    }  

}
