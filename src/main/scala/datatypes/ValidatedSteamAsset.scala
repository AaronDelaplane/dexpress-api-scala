package datatypes

case class ValidatedSteamAsset(
  //dexpress_id: UUID,
  classid:     String,
  instanceid:  String,
  appid:       Int,
  //contextid:   String, // todo ask Darius if this is needed
  assetid:     String,
  amount:      String,
)
