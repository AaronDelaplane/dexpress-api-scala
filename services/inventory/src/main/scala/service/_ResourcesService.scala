package service

import cats.effect._
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import service.clients.csfloat.{ClientCsFloat, ConfigCsFloat}
import service.clients.postgres.{ClientFlyway, ClientPostgres, ConfigPostgres}
import service.clients.steam.{ClientSteam, ConfigSteamClient}
import service.functions.effect._
import service.types._

object _ResourcesService {

  //private val logger = Slf4jLogger.getLogger[IO]
  
  def make(implicit CL: Clock[IO], CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, ResourcesService] =
    for {
      configService     <- Resource.liftF(_ConfigService.configValue.load)
      clientCsFloat     <- Resource.liftF(ConfigCsFloat.configValue.load).flatMap(ClientCsFloat.resource)
      configPg          <- Resource.liftF(ConfigPostgres.configValue.load)
      transactorPg       = Transactor.fromDriverManager(
                             "org.postgresql.Driver",
                             configPg.url,
                             configPg.user,
                             configPg.password.value
                           )
      clientPg           = new ClientPostgres(transactorPg)                      
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
        clientCsFloat,
        new ClientFlyway(flyway),
        clientPg, // todo remove if pkg functions.effectful is all that's needed
        clientSteam,
        new ToAssets(clientSteam, clientPg),
        new UpdateAssetTradingState(clientCsFloat, clientPg)
      )
}
