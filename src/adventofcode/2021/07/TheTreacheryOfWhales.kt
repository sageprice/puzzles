package adventofcode.`2021`.`07`

import java.io.File
import kotlin.math.abs
import kotlin.math.min

fun main() {
  val crabPositions: List<Long> =
    File("src/adventofcode/2021/07/input.txt")
      .readText()
      .split(",")
      .map { it.toLong() }

  // Part 1
  val highCrab = crabPositions.maxOf { it }
  val lowCrab = crabPositions.minOf { it }

  var m = Long.MAX_VALUE
  for (i in lowCrab..highCrab) {
    val fuelCost = crabPositions.sumOf { abs(it - i) }
    m = min(fuelCost, m)
  }
  println(m)

  // Part 2
  m = Long.MAX_VALUE
  for (i in lowCrab..highCrab) {
    val fuelCost = crabPositions.sumOf { p ->
      val d = abs(p - i)
      d * (d+1) / 2
    }
    m = min(fuelCost, m)
  }
  println(m)
}