import cats.data.NonEmptyList

package object datatypes {
  
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
  type VSA = ValidatedSteamAsset
  
  type SD  = SteamDescription
  type VSD = ValidatedSteamDescription
  
  type ST                = SteamTag
  type VSTWithoutColor   = ValidatedSteamTagWithoutColor
  type VSTWithMaybeColor = ValidatedSteamTagWithMaybeColor
  type VSTWithColor      = ValidatedSteamTagWithColor

}
