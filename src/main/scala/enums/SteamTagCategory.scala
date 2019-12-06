package enums

import enumeratum.{Enum, EnumEntry}

sealed abstract class SteamTagCategory(val category: String) extends EnumEntry

object SteamTagCategory extends Enum[SteamTagCategory] {
  val values = findValues

  case object Exterior extends SteamTagCategory("exterior")
  case object Rarity   extends SteamTagCategory("rarity")
  case object Typ      extends SteamTagCategory("type")
  case object Weapon   extends SteamTagCategory("weapon")
  case object Quality  extends SteamTagCategory("quality")
}