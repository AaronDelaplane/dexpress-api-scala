import cats.data.Writer
import cats.implicits._

type W[A] = Writer[Vector[String], A]

// non-effect
def fn1(n: Int): W[Int] =
  Writer(Vector("add ten"), n + 10)

def fn2(n: Int): W[Int] =
  Writer(Vector("add three"), n + 3)

def fn3(n: Int): W[Int] = for {
  a <- fn1(n)
  b <- fn2(a)
  _ <- Vector("just log").tell
  d <- 1000.writer(Vector("introducing 1000"))
  e <- Writer(Vector("adding 1000"), d + b)
} yield e

// retains log and adds 1 to value
def fn4(n: Int): W[Int] =
  fn1(n).map(_ + 1)

def fn5(n: Int): W[Int] =
  fn1(n)
    .flatMap(n => Writer(Vector("add three"), n + 3))
    .mapWritten(_ ++ Vector("just log"))
    .flatMap(n => 
      1000.writer(Vector("introducing 1000"))
        .flatMap(n2 => Writer(Vector("adding 1000"), n + n2))
    )

def slowly[A](body: => A) =
  try body finally Thread.sleep(100)

def factorial(n: Int): W[Int] = {
  slowly(
    if (n == 0) {
      val answer = 1
      Writer(Vector(s"fact $n $answer"), answer)
    } else {
      val answer = n - 1
      Writer(Vector(s"fact $n $answer"), answer * factorial(answer - 1).value)
    }
    
  )
  val ans = slowly(if(n == 0) 1 else n * factorial(n - 1))
  println(s"fact $n $ans")
  ans
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
Await.result(Future.sequence(Vector( Future(factorial(3)), Future(factorial(3))
)), 5.seconds)