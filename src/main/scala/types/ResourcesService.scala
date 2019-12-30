package types

import clients.csfloat.ClientCsFloat
import clients.postgres.{ClientFlyway, ClientPostgres}
import clients.steam.ClientSteam
import functions.effectful.GetAssets

final case class ResourcesService(
  // config
  configService:   ConfigService,
  // clients
  clientCsgoFloat: ClientCsFloat,
  clientFlyway:    ClientFlyway,
  clientPg:        ClientPostgres,
  clientSteam:     ClientSteam,
  getAssets:       GetAssets
)
