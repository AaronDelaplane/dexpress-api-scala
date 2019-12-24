package types

case class SteamAssetValidated(
  classid:     String,
  instanceid:  String,
  appid:       Int,
  //contextid:   String, // todo ask Darius if this is needed
  assetid:     String,
  amount:      String,
)
