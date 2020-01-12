import cats.data.State

val s1 = State[List[Int], Int] {
  state => 
    val state2 = state :+ 1
    (state2, state2.sum)
}

val s2 = State[List[Int], Int] {
  state =>
    val state2 = state :+ 200
    (state2, state2.sum)
}

val s3 = State[List[Int], Int] {
  state =>
    val state2 = state :+ 4000
    (state2, state2.sum)
}


val s4 = for {
  a <- s1
  b <- s2
  c <- s3
} yield (a, b, c)

