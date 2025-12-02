package adventofcode.`2025`.`02`

import java.io.File

/** https://adventofcode.com/2025/day/2 */
fun main() {
  val productIdRanges = File("src/adventofcode/2025/02/input.txt").readText()
    .split(",")
    .map { it.split("-") }

  println(productIdRanges.flatMap { bruteForce(it) }.sum())
  println(productIdRanges.flatMap { bruteForce2(it) }.sum())
}

private fun bruteForce(xs: List<String>): List<Long> {
  val goodRange = (xs.first().toLong()..xs.last().toLong())
  return goodRange.mapNotNull { x ->
    val xStr = "$x"
    val start = xStr.take(xStr.length / 2)
    val yStr = "$start$start"
    if (yStr == xStr) {
      val y = yStr.toLong()
      if (y in goodRange) {
        y
      } else null
    } else {
      null
    }
  }
}

private fun bruteForce2(xs: List<String>): List<Long> {
  val goodRange = (xs.first().toLong()..xs.last().toLong())
  val correct = goodRange.mapNotNull { x ->
    val good = mutableListOf<Long>()
    if (x < 10) return@mapNotNull null
    val xLen = x.toString().length
    for (i in 1..5) {
      val xReps = xLen / i
      if (xReps == 1) continue
      if (xReps * i == xLen) {
        val y = (1..xReps).joinToString("") { x.toString().take(i) }.toLong()
        if (y in goodRange) {
          good.add(y)
        }
      }
    }
    good
  }
  return correct.flatten().distinct()
}
