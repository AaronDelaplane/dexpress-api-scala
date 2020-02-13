import cats.data.State
import cats.implicits._



val s1 = State[List[Int], Int] {
  initialState => 
    val newState = initialState :+ 1
    println(s"state: $newState")
    (newState, newState.sum)
}

val s2 = State[List[Int], Int] {
  initialState =>
    val newState = initialState :+ 200
    println(s"state: $newState")
    (newState, newState.sum)
}

val s3 = State[List[Int], Int] {
  initialState =>
    val newState = initialState :+ 4000
    println(s"state: $newState")
    (newState, newState.sum)
}


val s4 = for {
  a <- s1
  b <- s2
  c <- s3
} yield (a, b, c)

s4.runA(List.empty[Int]).value
s4.runS(List.empty[Int]).value
