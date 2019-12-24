package types

final case class SteamDescription(
  // properties common to both assets and descriptions
  classid:          Option[String],
  instanceid:       Option[String],
  appid:            Option[Int],
  // description properties
  icon_url:         Option[String],
  tradable:         Option[Int],
  `type`:           Option[String],
  market_hash_name: Option[String],
  tags:             Option[List[SteamTag]],
  market_actions:   Option[List[SteamMarketAction]],
  descriptions:     Option[List[SteamNestedDescription]]
)
