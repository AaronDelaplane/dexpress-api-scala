package dexpress.types

import dexpress.clients.csfloat.ClientCsFloat
import dexpress.clients.postgres.{ClientFlyway, ClientPostgres}
import dexpress.clients.steam.ClientSteam
import dexpress.functions.effect.{ToAssets, UpdateAssetTradingState}

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
