package dexpress.enums

import enumeratum.{Enum, EnumEntry}

sealed abstract class ResourceName(override val entryName: String) extends EnumEntry

object ResourceName extends Enum[ResourceName] {
  val values = findValues
  
  case object Postgres extends ResourceName("postgresql")
  case object Steam extends ResourceName("steam web api")
}
