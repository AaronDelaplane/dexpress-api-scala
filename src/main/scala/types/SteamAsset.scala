package types

final case class SteamAsset(
  // properties common to both assets and descriptions
  classid:    Option[String],
  instanceid: Option[String],
  appid:      Option[Int],
  // asset properties
  contextid:  Option[String],
  assetid:    Option[String],
  amount:     Option[String]
)
