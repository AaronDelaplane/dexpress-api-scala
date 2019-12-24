package types

import org.http4s.Uri

final case class SteamDescriptionValidated(
  classid:          String,
  instanceid:       String,
  appid:            Int,
  market_hash_name: String,
  icon_url:         String,
  tradable:         Int,
  `type`:           String,
  link_id:          Option[String],
  sticker_urls:     Option[List[Uri]],
  tagExterior:      Option[STVWithoutColor],
  tagRarity:        Option[STVWithColor],
  tagType:          Option[STVWithoutColor],
  tagWeapon:        Option[STVWithoutColor],
  tagQuality:       Option[STVWithMaybeColor]
) 
