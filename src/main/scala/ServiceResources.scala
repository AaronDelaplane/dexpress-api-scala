import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.csfloat.{CsFloatClient, CsFloatConfig}
import clients.postgres.{FlywayClient, PostgresClient, PostgresConfig}
import clients.steam.{SteamClient, SteamClientConfig}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

final case class ServiceResources(
  // config
  serviceConfig: ServiceConfig,
  // clients
  csgoFloatClient: CsFloatClient,
  flywayClient:  FlywayClient,
  pgClient:      PostgresClient,
  steamClient:   SteamClient  
)

object ServiceResources {

  //private val logger = Slf4jLogger.getLogger[IO]
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, ServiceResources] =
    for {
      serviceConfig     <- Resource.liftF(ServiceConfig.configValue.load)
      csgoFloatConfig   <- Resource.liftF(CsFloatConfig.configValue.load)
      csgoFloatClient   <- CsFloatClient.resource(csgoFloatConfig)
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
      steamClientConfig <- Resource.liftF(SteamClientConfig.configValue.load)
      steamClient       <- SteamClient.resource(steamClientConfig)                     
    } yield 
      ServiceResources(
        serviceConfig,
        csgoFloatClient,
        new FlywayClient(flyway),
        new PostgresClient(pgTransactor),
        steamClient
      )
}
