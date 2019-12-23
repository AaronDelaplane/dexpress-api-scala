import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.csfloat.{ClientCsFloat, ConfigCsFloat}
import clients.postgres.{ClientFlyway, ClientPostgres, ConfigPostgres}
import clients.steam.{ClientSteam, ConfigSteamClient}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

final case class ResourcesService(
  // config
  configService:   ConfigService,
  // clients
  clientCsgoFloat: ClientCsFloat,
  clientFlyway:    ClientFlyway,
  clientPg:        ClientPostgres,
  clientSteam:     ClientSteam  
)

object ResourcesService {

  //private val logger = Slf4jLogger.getLogger[IO]
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, ResourcesService] =
    for {
      configService     <- Resource.liftF(ConfigService.configValue.load)
      clientCsgoFloat   <- Resource.liftF(ConfigCsFloat.configValue.load).flatMap(ClientCsFloat.resource)
      configPg          <- Resource.liftF(ConfigPostgres.configValue.load)
      transactorPg       = Transactor.fromDriverManager(
                             "org.postgresql.Driver",
                             configPg.url,
                             configPg.user,
                             configPg.password.value
                           )
      flyway            <- Resource.liftF(
                             IO.delay(
                               Flyway.configure()
                                 .dataSource(configPg.url, configPg.user, configPg.password.value)
                                 .load()
                             )
                           )
      configSteamClient <- Resource.liftF(ConfigSteamClient.configValue.load)
      clientSteam       <- ClientSteam.resource(configSteamClient)                     
    } yield 
      ResourcesService(
        configService,
        clientCsgoFloat,
        new ClientFlyway(flyway),
        new ClientPostgres(transactorPg),
        clientSteam
      )
}
