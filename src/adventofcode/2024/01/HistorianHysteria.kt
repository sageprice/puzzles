package adventofcode.`2024`.`01`

import java.io.File
import kotlin.math.abs

/** https://adventofcode.com/2024/day/1 */
fun main() {
  val input = File("src/adventofcode/2024/01/input.txt").readLines()
    .map { l -> l.split(" ").filter { it.isNotEmpty() }.map { it.toLong() } }

  // Part 1
  val firstDesc = input.map { it.first() }.sortedDescending()
  val secondDesc = input.map { it.last() }.sortedDescending()
  println(firstDesc.zip(secondDesc).sumOf { (a, b) -> abs(a - b) })

  // Part 2
  val secondCounts = secondDesc.groupBy { it }.mapValues { it.value.size }
  println(firstDesc.sumOf { it * (secondCounts[it] ?: 0) })
}