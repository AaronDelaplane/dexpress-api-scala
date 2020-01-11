package dexpress.types

final case class SteamTag(
  category:                Option[String],                  
  internal_name:           Option[String],             
  localized_category_name: Option[String],   
  localized_tag_name:      Option[String],
  color:                   Option[String] 
)
