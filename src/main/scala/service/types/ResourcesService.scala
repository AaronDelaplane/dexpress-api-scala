package service.types

import service.clients.csfloat.ClientCsFloat
import service.clients.postgres.{ClientFlyway, ClientPostgres}
import service.clients.steam.ClientSteam
import service.functions.effect.{ToAssets, UpdateAssetTradingState}

final case class ResourcesService(
  // config
  configService:           ConfigService,
  // clients
  clientCsgoFloat:         ClientCsFloat,
  clientFlyway:            ClientFlyway,
  clientPg:                ClientPostgres,
  clientSteam:             ClientSteam,
  toAssets:                ToAssets,
  updateAssetTradingState: UpdateAssetTradingState
)
