package adventofcode.`2024`.`11`

import java.io.File

/** https://adventofcode.com/2024/day/11 */
fun main() {
  val input = File("src/adventofcode/2024/11/input.txt").readText().split(" ").map { it.toLong() }

  var groupedRocks = input.toList().groupBy { it }.mapValues { (_, v) -> v.size.toLong() }
  repeat(25) {
    groupedRocks = blink(groupedRocks)
  }
  println(groupedRocks.values.sum())
  repeat(50) {
    groupedRocks = blink(groupedRocks)
  }
  println(groupedRocks.values.sum())

}

private fun blink(rocks: Map<Long, Long>): Map<Long, Long> {
  val outputRocks = mutableMapOf<Long, Long>()
  for ((rock, count) in rocks) {
    val newRocks: Map<Long, Long> = blink(rock).groupBy { it }.mapValues { (_, v) -> v.size.toLong() }
    newRocks.forEach { (r, c) ->
      outputRocks[r] = (outputRocks[r] ?: 0) + c * count
    }
  }
  return outputRocks
}

private fun blink(rock: Long): List<Long> {
  if (rock == 0L) {
    return listOf(1L)
  }
  return split(rock) ?: listOf(rock * 2024)
}

private fun split(rock: Long): List<Long>? {
  val rockStr = rock.toString()
  if (rockStr.length % 2 == 0) {
    return listOf(
      rockStr.substring(0, rockStr.length / 2).toLong(),
      rockStr.substring(rockStr.length / 2).toLong()
    )
  }
  return null
}

