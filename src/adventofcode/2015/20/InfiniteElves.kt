package adventofcode.`2015`.`20`

fun main() {
  val target = -1

  println(firstWithAtLeast(target))
  println(thoseLazyElves(target))
}

private fun firstWithAtLeast(k: Int): Int {
  val capacity = 800_000
  val presents = Array(capacity) { 0L }
  for (i in 1 until capacity) for (j in i until capacity step i) {
    presents[j] = presents[j] + 10 * i
  }
  for (i in presents.indices) {
    if (presents[i] > k) return i
  }
  throw IllegalStateException("Couldn't find it")
}

private fun thoseLazyElves(k: Int): Int {
  return (1..1_000_000).first { giftsForHouse(it) >= k }
}

private fun giftsForHouse(k: Int): Long {
  return 11L*(1L..50).sumOf { if (k % it == 0L) k / it else 0 }
}
