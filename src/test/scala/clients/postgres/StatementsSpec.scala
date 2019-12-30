package clients.postgres

import java.util.UUID

import cats.Monoid
import cats.effect.IO
import cats.instances.string.catsKernelStdMonoidForString
import clients.postgres.Statements._
import doobie._
import org.scalatest._
import types._

class StatementsSpec extends FunSuite with Matchers with doobie.scalatest.IOChecker {
  
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  
  val uuid            = UUID.randomUUID
  val dexpressAssetId = IdAsset(uuid)
  val refreshId       = IdRefresh(uuid)
  val steamId         = IdSteam(Monoid[String].empty)

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:assets", "postgres", "password"
  )

  test("insert assets rows") {
    check(insertAssets)
  }
  
  test("insert events_refresh_asset row") {
    check(insertEventRefreshAssets(refreshId, steamId, 0L))
  }
  
  test("select assets row by id_asset") {
    check(selectAsset(dexpressAssetId))
  }
  
  test("select assets rows by id_refresh") {
    check(selectAssets(refreshId))
  }
  
  test("select events_refresh_assets rows by id_steam") {
    check(selectEventsRefreshAssets(steamId))
  }
  
  test("update asset trading state") {
    check(updateAssetTradingState(dexpressAssetId, true))
  }
  
}
