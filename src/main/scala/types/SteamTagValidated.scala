package types

sealed trait SteamTagValidated {
  def category:                String                  
  def internal_name:           String             
  def localized_category_name: String   
  def localized_tag_name:      String
}

final case class SteamTagValidatedWithoutColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String
) extends SteamTagValidated

final case class SteamTagValidatedWithMaybeColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String,
  color:                   Option[String] 
) extends SteamTagValidated

final case class SteamTagValidatedWithColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String,
  color:                   String 
) extends SteamTagValidated
