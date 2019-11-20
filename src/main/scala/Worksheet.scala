import cats.MonadError
import cats.instances.either._


object Worksheet {
  
  type ErrorOr[A] = Either[String, A]

  MonadError[ErrorOr, String].raiseError("fail")
  
}
