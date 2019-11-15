package clients.sql

import cats.effect.IO
import org.flywaydb.core.Flyway

class FlywayClient(flyway: Flyway) {
  
  def migrate: IO[Unit] = IO.delay(flyway.migrate())
  
}
