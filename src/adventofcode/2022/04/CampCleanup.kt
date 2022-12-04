package adventofcode.`2022`.`04`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/04/input.txt")
    .readLines()
    .map {
      it.split(",")
        .map { p ->
          val xs = p.split("-")
          xs[0].toInt().. xs[1].toInt()
        } }
  println(input.count { (a, b) -> a contains b || b contains a })
  println(input.count { (a, b) -> (a intersect b).isNotEmpty() })
}

private infix fun IntRange.contains(other: IntRange): Boolean =
  other.first in this && other.last in this
