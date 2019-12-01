package common

final case class SteamInventory(
  assets:       Option[List[SteamAsset]],
  descriptions: Option[List[SteamDescription]]
)

final case class SteamAsset(
  appid:      Option[Int],
  contextid:  Option[String],
  assetid:    Option[String],
  classid:    Option[String],
  instanceid: Option[String],
  amount:     Option[String]
)

final case class SteamDescription(
  appid:            Option[Int],
  classid:          Option[String],
  instanceid:       Option[String],
  icon_url:         Option[String],
  tradable:         Option[Int],
  `type`:           Option[String],
  market_hash_name: Option[String],
  tags:             Option[List[SteamTag]],
  market_actions:   Option[List[SteamMarketAction]]
)

final case class SteamTag(
  category:           Option[String],
  localized_tag_name: Option[String]
)

final case class SteamMarketAction(
    link: Option[String]
)
