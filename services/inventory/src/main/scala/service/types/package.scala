package service

import java.util.UUID

import cats.data.NonEmptyList

package object types {
  
  /*
  generic types --------------------------------------------------------------------------------------------------------
   */
  type NEL[A]     = NonEmptyList[A]

  type ErrorOr[A] = Either[String, A]
  
  /*
  steam-related types --------------------------------------------------------------------------------------------------
   */
  type SI  = SteamInventory
  
  type SA  = SteamAsset
  type SAV = SteamAssetValidated
  
  type SD  = SteamDescription
  type SDV = SteamDescriptionValidated
  
  type ST                = SteamTag
  type STVWithoutColor   = SteamTagValidatedWithoutColor
  type STVWithMaybeColor = SteamTagValidatedWithMaybeColor
  type STVWithColor      = SteamTagValidatedWithColor
  
  /*
  value classes --------------------------------------------------------------------------------------------------------
   */
  final case class IdAsset(value: UUID) extends AnyVal
  final case class IdRefresh(value: UUID) extends AnyVal
  final case class IdSteam(value: String) extends AnyVal
  
  final case class StateTrading(value: Boolean) extends AnyVal
  
  final case class FloatValue(value: Double) extends AnyVal
}
