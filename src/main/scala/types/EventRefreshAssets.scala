package types

import java.util.UUID

final case class EventRefreshAssets(
  id_refresh: UUID,
  id_steam:   String,
  time:       Long
)
