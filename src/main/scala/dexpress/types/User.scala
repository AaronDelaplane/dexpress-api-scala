package dexpress.types

import java.util.UUID

final case class User(
  id_user:       UUID,
  id_user_steam: String,
  name_first:    String
)
