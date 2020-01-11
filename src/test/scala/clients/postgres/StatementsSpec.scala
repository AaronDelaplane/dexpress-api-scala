//package clients.postgres
//
//import java.util.UUID
//
//import cats.Monoid
//import cats.effect.IO
//import cats.instances.string.catsKernelStdMonoidForString
//import doobie._
//import org.scalatest._
//import dexpress.clients.postgres.Statements._
//import dexpress.types._
//
//class StatementsSpec extends FunSuite with Matchers with doobie.scalatest.IOChecker {
//  
//  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
//  
//  val uuid = UUID.randomUUID
//  val iA   = IdAsset(uuid)
//  val iR   = IdRefresh(uuid)
//  val iS   = IdSteam(Monoid[String].empty)
//
//  val transactor = Transactor.fromDriverManager[IO](
//    "org.postgresql.Driver", "jdbc:postgresql:inventory", "postgres", "password"
//  )
//  
//  test("delete assets by id_refresh") {
//    check(delete(iR))
//  }
//
//  test("insert assets rows") {
//    check(insertAssets)
//  }
//  
//  test("insert events_assets_refresh row") {
//    check(insertEventAssetsRefresh(iR, iS, 0L))
//  }
//  
//  test("select assets row by id_asset") {
//    check(selectAsset(iA))
//  }
//  
//  test("select assets rows by id_refresh") {
//    check(selectAssets(iR))
//  }
//  
//  test("select events_assets_refresh rows by id_steam") {
//    check(selectEventsAssetsRefresh(iS))
//  }
//
//  test("update asset trading state without floatvalue") {
//    check(updateAsset(iA, StateTrading(true)))
//  }
//  
//  test("update asset trading state with floatvalue") {
//    check(updateAsset(iA, StateTrading(true), FloatValue(0.0)))
//  }
//  
//}
