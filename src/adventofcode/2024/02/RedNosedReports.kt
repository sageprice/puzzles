package adventofcode.`2024`.`02`

import java.io.File

/** https://adventofcode.com/2024/day/2#part2 */
fun main() {
  val input =
    File("src/adventofcode/2024/02/input.txt").readLines().map { l ->
      l.split(" ").map { it.toInt() }
    }

  println(input.count { allChangesSafe(it, 1..3) || allChangesSafe(it, -3..-1) })
  println(input.count { dampenedChangesSafe(it, 1..3) || dampenedChangesSafe(it, -3..-1) })
}

private fun allChangesSafe(row: List<Int>, range: IntRange): Boolean =
  (1 until row.size).all { i -> row[i] - row[i-1] in range }

private fun dampenedChangesSafe(row: List<Int>, range: IntRange): Boolean {
  for (i in 1 until row.size) {
    if (row[i] - row[i-1] !in range) {
      return allChangesSafe(row.subList(0, i) + row.subList(i+1, row.size), range) ||
          allChangesSafe(row.subList(0, i-1) + row.subList(i, row.size), range)
    }
  }
  return true
}