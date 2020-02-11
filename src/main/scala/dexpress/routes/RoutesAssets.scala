package dexpress.routes

import cats.effect._
import cats.implicits._
import dexpress.codecs._
import dexpress.show._
import dexpress.types._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class RoutesAssets(resources: ResourcesService) extends Http4sDsl[IO] {
  
  import resources._
  
  implicit def logger = Slf4jLogger.getLogger[IO]

  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {

    case GET -> Root / "assets" 
      :? StateTradingQPM(stateTradingValidated) 
      +& SearchOffsetQPM(searchOffsetValidated)
      +& SearchLimitQPM(searchLimitValidated)
      +& MaybeSearchFilterQPM(maybeSearchFilterValidated)
      +& MaybeSearchFilterNotQPM(maybeSearchFilterNotValidated)
        =>
          (maybeSearchFilterValidated, maybeSearchFilterNotValidated) match {
            
            case (None, None) =>
              (stateTradingValidated, searchOffsetValidated, searchLimitValidated)
                .mapN((sT, sO, sL)      => toAssets.runToResponse(sT, sO, sL))
                .valueOr(parseFailures  => BadRequest(parseFailures.show))
            
            case (Some(searchFilterValidated), None) =>
              (stateTradingValidated, searchOffsetValidated, searchLimitValidated, searchFilterValidated)
                .mapN((sT, sO, sL, sFV) => toAssets.runToResponseFilter(sT, sO, sL, IdUser(sFV.value)))
                .valueOr(parseFailures  => BadRequest(parseFailures.show))
            
            case (None, Some(searchFilterNotValidated)) =>
              (stateTradingValidated, searchOffsetValidated, searchLimitValidated, searchFilterNotValidated)
                .mapN((sT, sO, sL, sFN) => toAssets.runToResponseFilterNot(sT, sO, sL, sFN))
                .valueOr(parseFailures  => BadRequest(parseFailures.show))
            
            case (Some(_), Some(_)) =>
              (stateTradingValidated, searchOffsetValidated, searchLimitValidated)
                .mapN((_, _, _)         => BadRequest("filter and filternot query parameters must not both be used")) // sql search
                .valueOr(parseFailures  => BadRequest(parseFailures.show))
        }
      
    case PATCH -> Root / "asset" :? IdAssetQPM(idAssetValidated) +& StateTradingQPM(stateTradingValidated) =>
      (idAssetValidated, stateTradingValidated)
        .mapN((iA, sT)         => updateAssetTradingState.run(iA, sT))
        .valueOr(parseFailures => BadRequest(parseFailures.show))
  
  }
}
