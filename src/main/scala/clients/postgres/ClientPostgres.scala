package clients.postgres

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
  
  def insertMany(xs: NEL[Asset], iR: IdRefresh, iS: IdSteam, time: Long): IO[Unit] = (
    for {
      _ <- Statements.insertAssets.updateMany(xs)
      _ <- Statements.insertEventRefreshAssets(iR, iS, time).run
    } yield ()
  ).transact(xa)
    
  def selectAsset(iA: IdAsset): IO[Asset] =
    Statements.selectAsset(iA).unique.transact(xa) 
  
  def selectAssets(iR: IdRefresh): IO[NEL[Asset]] =
    Statements.selectAssets(iR)
      .to[List]
      .transact(xa)
      .flatMap[NEL[Asset]](
        _.toNel.fold[IO[NEL[Asset]]](IO.raiseError(new Exception(s"no-assets-found-for-refresh-id: $iR")))(IO.pure(_))
      )
  
  def selectEventsRefreshAssets(iS: IdSteam): IO[Option[NEL[EventRefreshAssets]]] =
    Statements.selectEventsRefreshAssets(iS)
      .to[List]
      .transact(xa)
      .map(_.toNel)
  
  def updateAssetTradingState(iA: IdAsset, b: Boolean): IO[Int] =
    Statements.updateAssetTradingState(iA, b)
      .run
      .transact(xa)
  
}
