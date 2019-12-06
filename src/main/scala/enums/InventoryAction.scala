package enums

import enumeratum.{Enum, EnumEntry}

sealed trait InventoryAction extends EnumEntry

object InventoryAction extends Enum[InventoryAction] {
  val values = findValues
  case object refresh extends InventoryAction
}
