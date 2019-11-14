package clients.sql

import cats.effect.IO
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import org.http4s.Response
import org.http4s.dsl.Http4sDsl


class SQLClient(xa: Transactor[IO]) extends Http4sDsl[IO] {
  
  def testConnection: IO[Response[IO]] = 
    sql"select 1".query[Int].unique.transact(xa).attempt.flatMap {
      case Left(throwable) => InternalServerError(throwable.getMessage)
      case Right(_)        => NoContent()
    }
  
}
