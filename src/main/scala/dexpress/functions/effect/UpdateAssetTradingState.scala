package dexpress.functions.effect

import cats.effect.IO
import cats.instances.boolean._
import cats.syntax.eq._
import dexpress.clients.csfloat.ClientCsFloat
import dexpress.clients.postgres.ClientPostgres
import dexpress.codecs._
import dexpress.enums.ResourceName.Postgres
import dexpress.functions.noneffect.toHttpErrorResponse
import dexpress.types._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class UpdateAssetTradingState(cC: ClientCsFloat, cP: ClientPostgres) extends Http4sDsl[IO] {

  implicit def logger = Slf4jLogger.getLogger[IO]

  def run(iA: IdAsset, sT: StateTrading): IO[Response[IO]] =
    cP.selectExistingUniqueAsset(iA)
      .flatMap(
        _.fold(
          toHttpErrorResponse,
          assetA => {
            if (assetA.is_trading === sT.value) 
              toHttpErrorResponse(
                ResourceInvalidStateChangeError(Postgres, s"is_trading already set to (${sT.value}) for asset with id_asset (${iA.value})")
              )
            else if (sT.value) 
              (
                for {
                  fV       <- cC.toFloatValue(IdAssetSteam(assetA.id_asset_steam)).map(FloatValue.apply)
                  assetB   <- cP.updateAsset(iA, sT, fV)
                  _        <- logger.info(s"""
                                |update-asset-trading-state
                                |  id_asset:                 ${iA.value}
                                |  is_trading-value-initial: ${assetA.is_trading}
                                |  trading-value-received:   ${sT.value}
                                |  is_trading-value-current: ${assetB.is_trading}
                                |  float_value-initial:      ${assetA.float_value}
                                |  float_value-generated:    ${fV.value}
                                |  float_value-current:      ${assetB.float_value}
                                |""".stripMargin)
                  response <- Ok(assetB)
                } yield response
              ).handleErrorWith(toHttpErrorResponse)

            else 
              (
                for {
                  assetB   <- cP.updateAsset(iA, sT)
                  _        <- logger.info(s"""
                                |update-asset-trading-state
                                |  id_asset:                 ${iA.value}
                                |  is_trading-value-initial: ${assetA.is_trading}
                                |  trading-value-received:   ${sT.value}
                                |  is_trading-value-current: ${assetB.is_trading}
                                |""".stripMargin)
                  response <- Ok(assetB)
                } yield response
              ).handleErrorWith(toHttpErrorResponse)
          }
        )
      )

}
