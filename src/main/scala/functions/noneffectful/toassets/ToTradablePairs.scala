package functions.noneffectful.toassets

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import types._

object ToTradablePairs {

  private val Tradable = 1
   
  private val logger = Slf4jLogger.getLogger[IO]
  
  /*
  return nel of tradable assets & description pairs or error  
   */
  def toTradablePairsNel(ds: NEL[SDV], as: NEL[SAV]): IO[NEL[(SDV, SAV)]] =
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
  private def filter(ds: NEL[SDV], as: NEL[SAV]): ((List[SDV], List[SAV]), (List[SDV], List[SAV])) = {
    val (tDs, nTDs) = ds.toList.partition(isTradable)
    val (nTAs, tAs) = as.toList.partition(contains(nTDs))
    ((nTDs, nTAs), (tDs, tAs)) 
  }
  
  private def isTradable(x: SDV): Boolean =
    x.tradable === Tradable
  
  /*
  determine if a given asset is paired to a non-tradable description
   */
  private def contains(nTDs: List[SDV])(a: SAV): Boolean =
    nTDs.exists(isPair(a))
    
  private def isPair(a: SAV)(d: SDV): Boolean =
    (d.classid === a.classid) && (d.instanceid === a.instanceid)  
      
  private def log(ds: NEL[SDV], as: NEL[SAV], xs: ((List[SDV], List[SAV]), (List[SDV], List[SAV]))): IO[Unit] = {
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
  
  private def validate(ds: NEL[SDV], as: NEL[SAV], xs: ((List[SDV], List[SAV]), (List[SDV], List[SAV]))): ErrorOr[(NEL[SDV], NEL[SAV])] = {
    type R = (NEL[SDV], NEL[SAV])
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
  
  private def toPairs(ds: NEL[SDV], as: NEL[SAV]): (List[String], List[(SDV, SAV)]) = {
      as.toList.map(a =>
        ds
          .find(isPair(a))
          .fold("no-matching-description-for-asset".asLeft[(SDV, SAV)])(d => (d, a).asRight[String])
      ).separate 
  }
  
  private def log(errors: List[String], pairs: List[(SDV, SAV)]): IO[Unit] =
    logger.info(s"""
      |to-pairs-tradable-summary
      |  pairs-tradable-count: ${pairs.size}
      |  errors-count:         ${errors.size}  
      """.stripMargin)
  
  private def validate(errors: List[String], pairs: List[(SDV, SAV)]): ErrorOr[NEL[(SDV, SAV)]] =
    (NonEmptyList.fromList[String](errors), NonEmptyList.fromList(pairs)) match {
      case (None, None)            => "no-errors-or-assets-generated".asLeft[NEL[(SDV, SAV)]]
      case (Some(errors), Some(_)) => s"errors-&-assets-generated: $errors".asLeft[NEL[(SDV, SAV)]]
      case (Some(errors), None)    => s"only-errors-generated: $errors".asLeft[NEL[(SDV, SAV)]]
      case (None, Some(pairsNel))  => pairsNel.asRight[String]
    } 
 
}
