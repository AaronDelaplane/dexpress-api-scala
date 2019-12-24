import cats.data.NonEmptyList

package object types {
  
  /*
  generic types
   */
  type NEL[A]     = NonEmptyList[A]

  type ErrorOr[A] = Either[String, A]
  
  /*
  steam-related types
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

}
