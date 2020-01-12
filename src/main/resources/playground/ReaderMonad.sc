import cats.data.Reader

case class Cat(name: String, age: Int)

val fn1: Reader[Cat, String] =
  Reader(_.name)

val fn2: Reader[Cat, Int] =
  Reader(_.age)

val fn3 = for {
  a <- fn1
  b <- fn2
} yield s"$a is $b years old"

fn3(Cat("aaron", 3))


val a: Reader[Cat, String] = fn1.map(s => s"cat's name is $s")

val b: Reader[Cat, String] = fn1.flatMap(name => 
  fn2.map(age => s"$name is a $age year old cat")
)

b(Cat("aaron", 32))


case class Database(
  usernames: Map[Int, String],
  passwords: Map[String, String]
)



def findUsername(userId: Int): Reader[Database, Option[String]] =
  Reader(_.usernames.get(userId))

             
def checkPassword(username: String, password: String): Reader[Database, Boolean] =
  Reader(_.passwords.get(username).contains(password))

import cats.syntax.applicative._

type T[A] = Reader[Database, A]

def checkLogin(userId: Int, password: String): T[Boolean] =
  findUsername(userId)
    .flatMap(
      _.map(userName => checkPassword(userName, password))
        .getOrElse(false.pure[T])
    )




