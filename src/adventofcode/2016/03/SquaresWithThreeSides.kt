package adventofcode.`2016`.`03`

import java.io.File

fun main() {
  val triples = File("src/adventofcode/2016/03/input.txt").readLines().map { l ->
    val split = l.trim().split(" ").filter { it.isNotEmpty() && it.isNotBlank() }
    Triple(split.first().toInt(), split[1].toInt(), split.last().toInt())
  }
  var count = 0
  triples.forEach { (t1, t2, t3) ->
    val (a, b, c) = listOf(t1, t2, t3).sorted()
    if (a + b > c) {
      count++
    }
  }
  println(count)

  count = 0
  triples.chunked(3).forEach { l ->
    val (a1, b1, c1) = l.map { it.first }.sorted()
    if (a1 + b1 > c1) count++
    val (a2, b2, c2) = l.map { it.second }.sorted()
    if (a2 + b2 > c2) count++
    val (a3, b3, c3) = l.map { it.third }.sorted()
    if (a3 + b3 > c3) count++
  }
  println(count)
}