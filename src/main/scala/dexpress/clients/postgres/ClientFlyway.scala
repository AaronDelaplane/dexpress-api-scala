package dexpress.clients.postgres

import cats.effect.IO
import org.flywaydb.core.Flyway

class ClientFlyway(flyway: Flyway) {
  
  def migrate: IO[Int] = IO.delay(flyway.migrate())
  
}
