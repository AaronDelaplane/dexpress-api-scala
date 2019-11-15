import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.sql.{FlywayClient, PostgresClient, SQLConfig}
import clients.steam.{SteamClient, SteamClientConfig}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

// @formatter:off
final case class ServiceResources(
  serviceConfig: ServiceConfig,
  steamClient:   SteamClient,
  sqlClient:     PostgresClient,
  flywayClient:  FlywayClient  
)

object ServiceResources {
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, ServiceResources] =
    for {
      serviceConfig     <- Resource.liftF(ServiceConfig.configValue.load)
      steamClientConfig <- Resource.liftF(SteamClientConfig.configValue.load)
      steamClient       <- SteamClient.resource(steamClientConfig)
      sqlConfig         <- Resource.liftF(SQLConfig.configValue.load)
      sqlTransactor      = Transactor.fromDriverManager(
                             "org.postgresql.Driver",
                             sqlConfig.url,
                             sqlConfig.user,
                             sqlConfig.password.value
                           )
      flyway            <- Resource.liftF(
                             IO.delay(
                               Flyway.configure()
                                 .dataSource(sqlConfig.url, sqlConfig.user, sqlConfig.password.value)
                                 .load()
                             )
                           )
    } yield 
      ServiceResources(
        serviceConfig,
        steamClient,
        new PostgresClient(sqlTransactor),
        new FlywayClient(flyway)
      )
}


