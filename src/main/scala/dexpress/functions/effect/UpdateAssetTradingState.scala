package dexpress.functions.effect

import cats.effect.IO
import cats.instances.boolean._
import cats.syntax.eq._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import dexpress.clients.csfloat.ClientCsFloat
import dexpress.clients.postgres.ClientPostgres
import dexpress.codecs._
import dexpress.types._

class UpdateAssetTradingState(cC: ClientCsFloat, cP: ClientPostgres) extends Http4sDsl[IO] {

  val logger = Slf4jLogger.getLogger[IO]

  def run(iA: IdAsset, sT: StateTrading): IO[Response[IO]] =
    cP.selectAsset(iA).flatMap(assetA =>

      if (assetA.trading === sT.value) Conflict(s"asset trading state already set to (${sT.value})")

      else if (sT.value) for {
        fV       <- cC.toFloatValue(assetA.assetid).map(FloatValue.apply)
        assetB   <- cP.updateAsset(iA, sT, fV)
        _        <- logger.info(s"""
                      |update-asset-trading-state
                      |  asset-id:                     ${iA.value}
                      |  asset-trading-value-initial:  ${assetA.trading}
                      |  asset-trading-value-received: ${sT.value}
                      |  asset-trading-value-current:  ${assetB.trading}
                      |  asset-float-value-initial:    ${assetA.floatvalue}
                      |  asset-float-value-generated:  ${fV.value}
                      |  asset-float-value-current:    ${assetB.floatvalue}
                      |""".stripMargin)
        response <- Ok(assetB)
      } yield response  

      else for {
        assetB   <- cP.updateAsset(iA, sT)
        _        <- logger.info(s"""
                      |update-asset-trading-state
                      |  asset-id:                     ${iA.value}
                      |  asset-trading-value-initial:  ${assetA.trading}
                      |  asset-trading-value-received: ${sT.value}
                      |  asset-trading-value-current:  ${assetB.trading}
                      |""".stripMargin)
        response <- Ok(assetB)
      } yield response
    )

}
