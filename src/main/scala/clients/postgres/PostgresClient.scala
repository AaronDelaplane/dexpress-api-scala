package clients.postgres

import java.util.UUID

import cats.data.NonEmptyList
import cats.effect.IO
import datatypes.TradableAsset
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import doobie.postgres._
import doobie.postgres.implicits._

class PostgresClient(xa: Transactor[IO]) extends Http4sDsl[IO] {

implicit val han = LogHandler.jdkLogHandler  
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
  def insert(as: NonEmptyList[TradableAsset]): IO[Int] =
    Statements.insertAssets(as).updateMany(as).transact(xa)
    
  def selectAsset (assetId: UUID): IO[TradableAsset] =
    Statements.selectAsset(assetId).transact(xa) 
  
  def updateAssetTradingState(uuid: UUID, b: Boolean): IO[Int] =
    Statements.updateAssetTradingState(uuid, b).run.transact(xa)
  
}
