package enums

import enumeratum.{Enum, EnumEntry}

sealed trait ActionInventory extends EnumEntry

object ActionInventory extends Enum[ActionInventory] {
  val values = findValues
  case object refresh extends ActionInventory
}
