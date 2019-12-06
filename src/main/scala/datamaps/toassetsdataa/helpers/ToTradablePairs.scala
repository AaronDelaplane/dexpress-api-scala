package datamaps.toassetsdataa.helpers

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import datatypes._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object ToTradablePairs {

  private val Tradable = 1
   
  private val logger = Slf4jLogger.getLogger[IO]
  
  /*
  return nel of tradable assets & description pairs or error  
   */
  def toTradablePairsNel(ds: NEL[VSD], as: NEL[VSA]): IO[NEL[(VSD, VSA)]] =
    for {
      resultA    <- filter(ds, as).pure[IO]
      _          <- log(ds, as, resultA)
      validatedA <- validate(ds, as, resultA).fold(s => IO.raiseError(new Exception(s)), IO.pure)
      resultB     = toPairs(validatedA._1, validatedA._2)  
      _          <- log(resultB._1, resultB._2)
      validatedB <- validate(resultB._1, resultB._2).fold(s => IO.raiseError(new Exception(s)), IO.pure)
    } yield validatedB

  /*
  partition assets & descriptions into tradable and non-tradable lists
   */  
  private def filter(ds: NEL[VSD], as: NEL[VSA]): ((List[VSD], List[VSA]), (List[VSD], List[VSA])) = {
    val (tDs, nTDs) = ds.toList.partition(isTradable)
    val (nTAs, tAs) = as.toList.partition(contains(nTDs))
    ((nTDs, nTAs), (tDs, tAs)) 
  }
  
  private def isTradable(x: VSD): Boolean =
    x.tradable === Tradable
  
  /*
  determine if a given asset is paired to a non-tradable description
   */
  private def contains(nTDs: List[VSD])(a: VSA): Boolean =
    nTDs.exists(isPair(a))
    
  private def isPair(a: VSA)(d: VSD): Boolean =
    (d.classid === a.classid) && (d.instanceid === a.instanceid)  
      
  private def log(ds: NEL[VSD], as: NEL[VSA], xs: ((List[VSD], List[VSA]), (List[VSD], List[VSA]))): IO[Unit] = {
    val ((nTDs, nTAs), (tDs, tAs)) = xs
    logger.info(s"""
      |filter-tradable-assets-&-descriptions-summary
      |  descriptions-count:              ${ds.size}
      |  descriptions-tradable-count:     ${tDs.size}
      |  descriptions-non-tradable-count: ${nTDs.size}
      |  assets-count:                    ${as.size}
      |  assets-tradable-count:           ${tAs.size}
      |  assets-non-tradable-count:       ${nTAs.size}    
    """.stripMargin)
  }
  
  private def validate(ds: NEL[VSD], as: NEL[VSA], xs: ((List[VSD], List[VSA]), (List[VSD], List[VSA]))): ErrorOr[(NEL[VSD], NEL[VSA])] = {
    type R = (NEL[VSD], NEL[VSA])
    val ((nTDs, nTAs), (tDs, tAs)) = xs
    if (ds.size =!= nTDs.size + tDs.size) 
      "descriptions-count-does-not-equal-non-tradable-plus-tradable-descriptions-count".asLeft[R]
    else if (as.size =!= nTAs.size + tAs.size)
      "assets-count-does-not-equal-non-tradable-plus-tradable-assets-count".asLeft[R]
    (tDs.toNel, tAs.toNel) match {
      case (None, None)                 => "no-tradable-assets-or-descriptions".asLeft[R]
      case (None, _)                    => "no-tradable-assets".asLeft[R]
      case (_, None)                    => "no-tradable-descriptions".asLeft[R]
      case (Some(tDsNel), Some(tAsNel)) => (tDsNel, tAsNel).asRight[String]
    }
  }
  
  private def toPairs(ds: NEL[VSD], as: NEL[VSA]): (List[String], List[(VSD, VSA)]) = {
      as.toList.map(a =>
        ds
          .find(isPair(a))
          .fold("no-matching-description-for-asset".asLeft[(VSD, VSA)])(d => (d, a).asRight[String])
      ).separate 
  }
  
  private def log(errors: List[String], pairs: List[(VSD, VSA)]): IO[Unit] =
    logger.info(s"""
      |to-pairs-tradable-summary
      |  pairs-tradable-count: ${pairs.size}
      |  errors-count:         ${errors.size}  
      """.stripMargin)
  
  private def validate(errors: List[String], pairs: List[(VSD, VSA)]): ErrorOr[NEL[(VSD, VSA)]] =
    (NonEmptyList.fromList[String](errors), NonEmptyList.fromList(pairs)) match {
      case (None, None)            => "no-errors-or-assets-generated".asLeft[NEL[(VSD, VSA)]]
      case (Some(errors), Some(_)) => s"errors-&-assets-generated: $errors".asLeft[NEL[(VSD, VSA)]]
      case (Some(errors), None)    => s"only-errors-generated: $errors".asLeft[NEL[(VSD, VSA)]]
      case (None, Some(pairsNel))  => pairsNel.asRight[String]
    } 
 
}
