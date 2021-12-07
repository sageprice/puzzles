package adventofcode.`2021`.`06`

import java.io.File

fun main() {
  val daysToSpawn: List<Int> =
    File("src/adventofcode/2021/06/input.txt")
      .readLines()
      .map { line ->
        line.split(",").map { it.toInt() }
      }.first()

  var counts = LongArray(9)
  for (d in daysToSpawn) {
    counts[d]++
  }
  // Day 1
  repeat(80) { counts = passADay(counts) }
  println(counts.sum())

  // Day 2
  repeat(256-80) { counts = passADay(counts) }
  println(counts.sum())
}

private fun passADay(counts: LongArray): LongArray {
  val nextCounts = LongArray(9)
  for (i in 1..8) {
    nextCounts[i-1] = counts[i]
  }
  nextCounts[8] += counts[0]
  nextCounts[6] += counts[0]
  return nextCounts
}