package service.types

final case class SteamInventory(
  assets:       Option[List[SteamAsset]],
  descriptions: Option[List[SteamDescription]]
)

final case class SteamMarketAction(
  link: Option[String]
)

final case class SteamNestedDescription(
  `type`: Option[String],
   value: Option[String]
)
