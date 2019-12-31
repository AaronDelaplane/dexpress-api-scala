package service.types

import eu.timepit.refined.types.net.UserPortNumber
import org.http4s.Uri

final case class ConfigService(
  httpHost: Uri,
  httpPort: UserPortNumber
)
