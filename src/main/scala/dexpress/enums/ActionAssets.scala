package dexpress.enums

import enumeratum.{Enum, EnumEntry}

sealed trait ActionAssets extends EnumEntry

object ActionAssets extends Enum[ActionAssets] {
  val values = findValues
  case object Get extends ActionAssets
}
