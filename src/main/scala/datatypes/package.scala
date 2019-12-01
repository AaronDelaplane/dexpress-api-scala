import cats.data.NonEmptyList

package object datatypes {
  
  type NEL[A]     = NonEmptyList[A]
  
  type ErrorOr[A] = Either[String, A]

}
