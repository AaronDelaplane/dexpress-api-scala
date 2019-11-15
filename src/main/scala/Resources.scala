import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.sql.{FlywayClient, PostgresClient, SQLConfig}
import clients.steam.{SteamClient, SteamClientConfig}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

final case class Resources(
  serviceConfig: ServiceConfig,
  steamClient:   SteamClient,
  sqlClient:     PostgresClient,
  flywayClient:  FlywayClient  
)

object Resources {
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, Resources] =
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
      Resources(
        serviceConfig,
        steamClient,
        new PostgresClient(sqlTransactor),
        new FlywayClient(flyway)
      )
}


