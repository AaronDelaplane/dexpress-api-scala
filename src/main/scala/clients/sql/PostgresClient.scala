package clients.sql

import cats.data.NonEmptyList
import cats.effect.IO
import clients.data.Asset
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.http4s.Response
import org.http4s.dsl.Http4sDsl

class PostgresClient(xa: Transactor[IO]) extends Http4sDsl[IO] {
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
  def insertAssets(assets: NonEmptyList[Asset]): IO[Int] =
    Statements.insertAssets(assets).transact(xa)
  
}
