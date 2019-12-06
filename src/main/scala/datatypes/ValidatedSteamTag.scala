package datatypes

sealed trait ValidatedSteamTag {
  def category:                String                  
  def internal_name:           String             
  def localized_category_name: String   
  def localized_tag_name:      String
}

final case class ValidatedSteamTagWithoutColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String
) extends ValidatedSteamTag

final case class ValidatedSteamTagWithMaybeColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String,
  color:                   Option[String] 
) extends ValidatedSteamTag

final case class ValidatedSteamTagWithColor(
  category:                String,                  
  internal_name:           String,             
  localized_category_name: String,   
  localized_tag_name:      String,
  color:                   String 
) extends ValidatedSteamTag
