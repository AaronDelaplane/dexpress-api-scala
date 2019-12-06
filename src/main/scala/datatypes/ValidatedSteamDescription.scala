package datatypes

import org.http4s.Uri

final case class ValidatedSteamDescription(
  classid:          String,
  instanceid:       String,
  appid:            Int,
  market_hash_name: String,
  icon_url:         String,
  tradable:         Int,
  `type`:           String,
  link_id:          Option[String],
  sticker_urls:     Option[List[Uri]],
  tagExterior:      Option[VSTWithoutColor],
  tagRarity:        Option[VSTWithColor],
  tagType:          Option[VSTWithoutColor],
  tagWeapon:        Option[VSTWithoutColor],
  tagQuality:       Option[VSTWithMaybeColor]
) 
