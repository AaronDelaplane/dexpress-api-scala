package datamaps.toassetsdataa.helpers

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import datatypes._
import enums.SteamTagCategory
import enums.SteamTagCategory._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.Uri

import scala.util.matching.Regex

object ToValidatedSteamDescriptions {

  private val logger = Slf4jLogger.getLogger[IO]
  
  def toValidatedSDsNel(xs: NEL[SD]): IO[NEL[VSD]] = {
    val (errors, vSDs) = xs.toList.partitionMap(toValidatedSD)
    for {
      _   <- log(errors, vSDs)
      nel <- validate(errors, vSDs).fold(s => IO.raiseError(new Exception(s)), IO.pure)
    } yield nel
  }
  
  private def toValidatedSD(x: SD): ErrorOr[VSD] =
    for {
      _log_id          <- s"classid=${x.classid}_instanceid=${x.instanceid}".asRight
      
      classid          <- x.classid.fold(s"description.classid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      instanceid       <- x.instanceid.fold(s"description.instanceid-not-defined--${_log_id}".asLeft[String])(_.asRight)
      appid            <- x.appid.fold(s"description.appid-not-defined--${_log_id}".asLeft[Int])(_.asRight)
      
      market_hash_name <- x.market_hash_name.fold(s"description.market_hash_name-not-defined--${_log_id}".asLeft[String])(_.asRight)
      icon_url         <- x.icon_url.fold(s"description.icon_url-not-defined--${_log_id}".asLeft[String])(_.asRight)
      tradable         <- x.tradable.fold(s"description.tradable-not-defined--${_log_id}".asLeft[Int])(_.asRight)
      typ              <- x.`type`.fold(s"description.type-not-defined--${_log_id}".asLeft[String])(_.asRight)
      link_id           = x.market_actions.flatMap(_.headOption).flatMap(_.link).flatMap(toMaybeLinkId)
      sticker_urls     <- x.descriptions.flatMap(toMaybeStickerUrls).map(_.toList).asRight

      _tags            <- x.tags.fold(s"tags-not-defined--${_log_id}".asLeft[List[SteamTag]])(_.asRight)
      _neTags          <- _tags.toNel.fold(s"tags-empty--${_log_id}".asLeft[NEL[SteamTag]])(_.asRight)
      tagExterior      <- findTag(_neTags, Exterior).map(toVSTWithoutColor)
                            .fold(Option.empty[VSTWithoutColor].asRight[String])({
                              case Left(error) => error.asLeft[Option[VSTWithoutColor]]
                              case Right(vst)  => vst.some.asRight[String]
                            })
      tagRarity        <- findTag(_neTags, Rarity).map(toVSTWithColor)
                            .fold(Option.empty[VSTWithColor].asRight[String])({
                              case Left(error) => error.asLeft[Option[VSTWithColor]]
                              case Right(vst)  => vst.some.asRight[String]
                            })   
      tagType          <- findTag(_neTags, Typ).map(toVSTWithoutColor)
                            .fold(Option.empty[VSTWithoutColor].asRight[String])({
                              case Left(error) => error.asLeft[Option[VSTWithoutColor]]
                              case Right(vst)  => vst.some.asRight[String]
                            })                                         
      tagWeapon        <- findTag(_neTags, Weapon).map(toVSTWithoutColor)
                           .fold(Option.empty[VSTWithoutColor].asRight[String])({
                             case Left(error) => error.asLeft[Option[VSTWithoutColor]]
                             case Right(vst)  => vst.some.asRight[String]
                           })                                        
      tagQuality       <- findTag(_neTags, Quality).map(toVSTWithMaybeColor)
                            .fold(Option.empty[VSTWithMaybeColor].asRight[String])({
                              case Left(error) => error.asLeft[Option[VSTWithMaybeColor]]
                              case Right(vst)  => vst.some.asRight[String]
                            })                                                                              
    } yield
      ValidatedSteamDescription(
        classid          = classid,
        instanceid       = instanceid,
        appid            = appid,
        market_hash_name = market_hash_name,
        icon_url         = icon_url,
        tradable         = tradable,
        `type`           = typ,
        link_id          = link_id,
        sticker_urls     = sticker_urls,
        tagExterior      = tagExterior,
        tagRarity        = tagRarity,
        tagType          = tagType,
        tagWeapon        = tagWeapon,
        tagQuality       = tagQuality,
      )
  
  private def log(errors: List[String], xs: List[VSD]): IO[Unit] =
    logger.info(s"""
      |to-validated-steam-descriptions-nel-summary
      |  validated-steam-descriptions-count: ${xs.size}
      |  erors-count:                        ${errors.size}
      |  errors:                             $errors
      |  with-tag-exterior-count:            ${xs.filter(_.tagExterior.isDefined).size}
      |  with-tag-rarity-count:              ${xs.filter(_.tagRarity.isDefined).size}
      |  with-tag-type-count:                ${xs.filter(_.tagType.isDefined).size}
      |  with-tag-weapon-count:              ${xs.filter(_.tagWeapon.isDefined).size}
      |  with-tag-quality-count:             ${xs.filter(_.tagQuality.isDefined).size}
    """.stripMargin)
     
  private def validate(errors: List[String], xs: List[VSD]): ErrorOr[NEL[VSD]] =
    (errors.toNel, xs.toNel) match {
      case (Some(_), _)      => "attempt-to-trasform-steam-descriptions-to-validated-steam-descriptions-failed".asLeft[NEL[VSD]]
      case (None, None)      => "attempt-to-transfrom-steam-descriptions-to-validated-steam-descriptions-returned-zero-results".asLeft[NEL[VSD]]
      case (None, Some(nel)) => nel.asRight[String]
    }

  private def toMaybeLinkId(s: String): Option[String] =
      s.split("%D").reverse.headOption 
    
  private val uriRegex = new Regex("https.*?png")
  private def toMaybeStickerUrls(xs: List[SteamNestedDescription]): Option[NonEmptyList[Uri]] =
    xs.flatMap(_.value).foldLeft(List.empty[Uri])((acc, string) => 
      acc ++ uriRegex.findAllIn(string).toList.flatMap(s => Uri.fromString(s) match {
        case Right(result) => acc :+ result
        case Left(_)       => acc
      })
    ).toNel  
  
  private def log(xs: NEL[ST]): IO[Unit] =
    logger.info(s"""
       |tags-summary
       |  count: ${xs.size} 
       |""".stripMargin)
    
  private def findTag(nel: NEL[ST], c: SteamTagCategory): Option[ST] =
    nel.find(_.category.map(_.toLowerCase === c.category).getOrElse(false))

  private def toVSTWithoutColor(x: ST): ErrorOr[VSTWithoutColor] = {
    for {
      category                <- toTCategory(x)                  
      internal_name           <- toTInternalName(x)               
      localized_category_name <- toTLocalizedCategoryName(x)    
      localized_tag_name      <- toTLocalizedTagName(x)
    } yield ValidatedSteamTagWithoutColor(
      category                = category,
      internal_name           = internal_name,
      localized_category_name = localized_category_name,
      localized_tag_name      = localized_tag_name     
    )
  }
  
  private def toVSTWithMaybeColor(x: ST): ErrorOr[VSTWithMaybeColor] = {
    for {
      category                <- toTCategory(x)                  
      internal_name           <- toTInternalName(x)               
      localized_category_name <- toTLocalizedCategoryName(x)    
      localized_tag_name      <- toTLocalizedTagName(x)
    } yield ValidatedSteamTagWithMaybeColor(
      category                = category,
      internal_name           = internal_name,
      localized_category_name = localized_category_name,
      localized_tag_name      = localized_tag_name,
      color                   = x.color     
    )
  }
    
  private def toVSTWithColor(x: ST): ErrorOr[VSTWithColor] = {
    for {
      category                <- toTCategory(x)                  
      internal_name           <- toTInternalName(x)               
      localized_category_name <- toTLocalizedCategoryName(x)    
      localized_tag_name      <- toTLocalizedTagName(x)
      color                   <- x.localized_tag_name.fold("tag-color-not-defined".asLeft[String])(_.asRight[String])  
    } yield ValidatedSteamTagWithColor(
      category                = category,
      internal_name           = internal_name,
      localized_category_name = localized_category_name,
      localized_tag_name      = localized_tag_name,
      color                   = color     
    )
  }
  
  private def toTCategory(x: ST): ErrorOr[String] =
    x.category.fold("tag-category-not-defined".asLeft[String])(_.asRight[String])
  
  private def toTInternalName(x: ST): ErrorOr[String] =
    x.internal_name.fold("tag-internal_name-not-defined".asLeft[String])(_.asRight[String])  
    
  private def toTLocalizedCategoryName(x: ST): ErrorOr[String] =
    x.localized_category_name.fold("tag-localized_category_name-not-defined".asLeft[String])(_.asRight[String])
    
  private def toTLocalizedTagName(x: ST): ErrorOr[String] =
    x.localized_tag_name.fold("tag-localized_tag_name-not-defined".asLeft[String])(_.asRight[String])      
    
}
