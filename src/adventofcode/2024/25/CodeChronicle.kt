package adventofcode.`2024`.`25`

import java.io.File

/** https://adventofcode.com/2024/day/25 */
fun main() {
  val input = File("src/adventofcode/2024/25/input.txt").readText().split("\n\n")
  val keys = mutableListOf<List<Int>>()
  val locks = mutableListOf<List<Int>>()
  for (chunk in input) {
    val lines = chunk.split("\n")
    val bitsPerCol = lines.first().indices.map { c ->
      lines.count { it[c] == '#' }
    }
    if (lines[0][0] == '#') {
      locks.add(bitsPerCol)
    } else {
      keys.add(bitsPerCol)
    }
  }
  var compatibleCount = 0
  for (key in keys) for (lock in locks) {
    if (key.zip(lock).all { (a, b) -> a + b <= 7 }) {
      compatibleCount++
    }
  }
  println(compatibleCount)
}