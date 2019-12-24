package types

import clients.csfloat.ClientCsFloat
import clients.postgres.{ClientFlyway, ClientPostgres}
import clients.steam.ClientSteam
import functions_io.GetAssets

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
