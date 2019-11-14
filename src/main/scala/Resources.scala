import cats.effect.{ConcurrentEffect, ContextShift, IO, Resource}
import clients.sql.{SQLClient, SQLClientConfig}
import clients.steam.{SteamClient, SteamClientConfig}
import doobie.util.transactor.Transactor

final case class Resources(
  serviceConfig: ServiceConfig,
  steamClient:   SteamClient,
  sqlClient:     SQLClient
)

object Resources {
  
  def make(implicit CE: ConcurrentEffect[IO], CS: ContextShift[IO]): Resource[IO, Resources] =
    for {
      serviceConfig     <- Resource.liftF(ServiceConfig.configValue.load)
      steamClientConfig <- Resource.liftF(SteamClientConfig.configValue.load)
      steamClient       <- SteamClient.resource(steamClientConfig)
      sqlConfig         <- Resource.liftF(SQLClientConfig.configValue.load)
      sqlTransactor      = Transactor.fromDriverManager(
                             "org.postgresql.Driver",
                             sqlConfig.url,
                             sqlConfig.user,
                             sqlConfig.password.value
                           )
    } yield Resources(serviceConfig, steamClient, new SQLClient(sqlTransactor))
}


