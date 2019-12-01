import cats.effect.IO
import clients.postgres.Statements._
import doobie._
import org.scalatest._

class StatementsSpec extends FunSuite with Matchers with doobie.scalatest.IOChecker {
  
  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val transactor = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:inventory", "postgres", "password"
  )

  test("update asset trading state") { 
    check(updateAssetTradingState(java.util.UUID.randomUUID, true))
  }

}
