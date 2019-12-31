package service

import cats.data.NonEmptyList
import cats.implicits._
import cats.{Monoid, Show}

package object show {
  
  /*
  show instances -------------------------------------------------------------------------------------------------------
   */
  implicit def nelStringShow: Show[NonEmptyList[String]] = Show.show[NonEmptyList[String]](
    _.foldLeft("\n")(_ + _ + "\n")
  )

  implicit def parseFailureNelShow: Show[NonEmptyList[org.http4s.ParseFailure]] =
    Show.show[NonEmptyList[org.http4s.ParseFailure]](_.map(_.sanitized).show)

  implicit def parseFailureShow: Show[org.http4s.ParseFailure] =
    Show.show[org.http4s.ParseFailure](_.sanitized)

  /*
  implicit classes -----------------------------------------------------------------------------------------------------
   */
  // generic for List[A]
  implicit class ShowList[A](as: List[A])(implicit s: Show[A]) {
    def listShow: Show[List[A]] = Show.show(_.foldLeft(Monoid[String].empty)(_ + _.show))
  }
}
