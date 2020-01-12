package dexpress.types

case class SteamAssetValidated(
  id_class:       String,
  id_instance:    String,
  id_app:         Int, 
//contextid:      String, // todo ask Darius if this is needed
  id_asset_steam: String,
  amount:         String,
)

object SteamAssetValidated {
  val empty = SteamAssetValidated("", "", 0, "", "")
}
