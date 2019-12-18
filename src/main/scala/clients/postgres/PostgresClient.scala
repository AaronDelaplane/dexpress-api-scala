package clients.postgres

import java.util.UUID

import cats.effect.IO
import datatypes._
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class PostgresClient(xa: Transactor[IO]) extends Http4sDsl[IO] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler  
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
  def insertMany(xs: NEL[Asset]): IO[Int] =
    Statements.insertAssets.updateMany(xs).transact(xa) // todo remove argument to insertAssets
    
  def selectAsset(assetId: UUID): IO[Asset] =
    Statements.selectAsset(assetId).unique.transact(xa) 
  
//  def insert(x: AssetDataB): IO[Int] =
//    Statements.insertAssetDataB.run(x).transact(xa)
  
//  def updateAssetTradingState(uuid: UUID, b: Boolean): IO[Int] =
//    Statements.updateAssetTradingState(uuid, b).run.transact(xa)
  
}
