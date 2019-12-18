package datatypes

import java.util.UUID

final case class EventRefreshAssets(
  refresh_id: UUID,
  time:       Long
)
