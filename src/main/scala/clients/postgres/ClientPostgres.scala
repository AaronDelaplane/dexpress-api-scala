package clients.postgres

import java.util.UUID

import cats.effect.IO
import cats.syntax.list._
import doobie.implicits._
import doobie.util.log.LogHandler
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import types._

class ClientPostgres(xa: Transactor[IO]) extends Http4sDsl[IO] {

  implicit val han: LogHandler = LogHandler.jdkLogHandler  
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
  def insertMany(xs: NEL[Asset], refreshId: UUID, steamId: String, time: Long): IO[Unit] = (
    for {
      _ <- Statements.insertAssets.updateMany(xs)
      _ <- Statements.insertEventRefreshAssets(refreshId, steamId, time).run
    } yield ()
  ).transact(xa)
    
  def selectAsset(assetId: UUID): IO[Asset] =
    Statements.selectAsset(assetId).unique.transact(xa) 
  
  def selectAssets(refreshId: UUID): IO[NEL[Asset]] =
    Statements.selectAssets(refreshId).to[List].transact(xa)
    .flatMap[NEL[Asset]](
      _.toNel.fold[IO[NEL[Asset]]](IO.raiseError(new Exception(s"no-assets-found-for-refresh-id: $refreshId")))(IO.pure(_)))
     
  
  def selectEventsRefreshAssets(steamId: String): IO[Option[NEL[EventRefreshAssets]]] =
    Statements.selectEventsRefreshAssets(steamId).to[List].transact(xa).map(_.toNel)
  
//  def insert(x: AssetDataB): IO[Int] =
//    Statements.insertAssetDataB.run(x).transact(xa)
  
//  def updateAssetTradingState(uuid: UUID, b: Boolean): IO[Int] =
//    Statements.updateAssetTradingState(uuid, b).run.transact(xa)
  
}
