package clients.postgres

import cats.Monoid
import cats.effect.IO
import cats.instances.string.catsKernelStdMonoidForString
import dexpress.clients.postgres.Statements._
import dexpress.functions.noneffect.randomUUIDF
import dexpress.types._
import doobie._
import org.scalatest._

class StatementsSpec extends FunSuite with Matchers with doobie.scalatest.IOChecker {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val uuid = randomUUIDF.unsafeRunSync
  val iA   = IdAsset(uuid)
  val iR   = IdRefresh(uuid)
  val iU   = IdUser(uuid)
  val iUS  = IdUserSteam(Monoid[String].empty)
  val user = User(uuid, "", "")
  val sT   = StateTrading(true)

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:dexpress", "postgres", "password"
  )

  test("delete(iR: IdRefresh)") {
    check(delete(iR))
  }
  
  test("exists(iUS: IdUserSteam)") {
    check(exists(iUS))
  }

  test("insertAssets()") {
    check(insertAssets)
  }

  test("insertEventAssetsRefresh(iR: IdRefresh, iU: IdUser, time: Long)") {
    check(insertEventAssetsRefresh(iR, iU, 0L))
  }
  
  test("insertUser(u: User)") {
    check(insertUser(user))
  }

  test("selectAsset(iA: IdAsset)") {
    check(selectAsset(iA))
  }
  
  test("selectAssets(sT: StateTrading)") {
    check(selectAssets(sT))
  }
  
  test("selectAssetsFilter(sT: StateTrading, iR: IdRefresh)") {
    check(selectAssetsFilter(sT, iR))
  }
  
  test("selectAssetsFilterNot(sT: StateTrading, iU: IdUser)") {
    check(selectAssetsFilterNot(sT, iU))
  }

  test("selectEventsAssetsRefresh(iU: IdUser)") {
    check(selectEventsAssetsRefresh(iU))
  }
  
  test("selectUser(iU: IdUser)") {
    check(selectUser(iU))
  }
  
  test("selectUser(iUS: IdUserSteam)") {
    check(selectUser(iUS))
  }

  test("updateAsset(iA: IdAsset, sT: StateTrading)") {
    check(updateAsset(iA, sT))
  }

  test("uupdateAsset(iA: IdAsset, sT: StateTrading, fV: FloatValue)") {
    check(updateAsset(iA, sT, FloatValue(0.0)))
  }

}
