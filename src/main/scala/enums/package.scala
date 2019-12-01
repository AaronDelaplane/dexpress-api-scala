import enumeratum.{Enum, EnumEntry}

package object enums {
  
  sealed trait InventoryAction extends EnumEntry
  
  object InventoryAction extends Enum[InventoryAction] {
    val values = findValues
    case object refresh extends InventoryAction
  }
  
}
