import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.postgres.{FlywayClient, PostgresClient, PostgresConfig}
import clients.steam.{SteamClient, SteamClientConfig}
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.flywaydb.core.Flyway

final case class ServiceResources(
  serviceConfig: ServiceConfig,
  steamClient:   SteamClient,
  sqlClient:     PostgresClient,
  flywayClient:  FlywayClient  
)

object ServiceResources {

  val log = Slf4jLogger.getLogger[IO]
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, ServiceResources] =
    for {
      serviceConfig     <- Resource.liftF(ServiceConfig.configValue.load)
      steamClientConfig <- Resource.liftF(SteamClientConfig.configValue.load)
      steamClient       <- SteamClient.resource(steamClientConfig)
      pgConfig          <- Resource.liftF(PostgresConfig.configValue.load)
      pgTransactor       = Transactor.fromDriverManager(
                              "org.postgresql.Driver",
                              pgConfig.url,
                              pgConfig.user,
                              pgConfig.password.value
                            )
      flyway            <- Resource.liftF(
                             IO.delay(
                               Flyway.configure()
                                 .dataSource(pgConfig.url, pgConfig.user, pgConfig.password.value)
                                 .load()
                             )
                           )
    } yield 
      ServiceResources(
        serviceConfig,
        steamClient,
        new PostgresClient(pgTransactor),
        new FlywayClient(flyway)
      )
}


