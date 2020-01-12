package dexpress.types

import java.util.UUID

final case class EventAssetsRefresh(
  id_refresh: UUID,
  id_user:    UUID,
  time:       Long
)
