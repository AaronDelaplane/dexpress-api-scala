package service.clients.postgres

import cats.effect.IO
import org.flywaydb.core.Flyway

class ClientFlyway(flyway: Flyway) {
  
  def migrate: IO[Unit] = IO.delay(flyway.migrate())
  
}
