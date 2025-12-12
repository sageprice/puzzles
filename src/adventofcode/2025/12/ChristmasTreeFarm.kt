package adventofcode.`2025`.`12`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2025/12/input.txt").readText()
    .split("\n\n")
  val regions = input.last().split("\n").map { parseRegion(it) }

  var total = 0
  for (idx in regions.indices) {
    val r = regions[idx]
    if (canArrange(r)) {
      total++
    }
  }
  println(total)
}

private fun canArrange(region: Region): Boolean {
  return canFillCheatingGuess(region)
}

/** Lazy test for whether the total space exceeds the amount of points we need to populate */
private fun canFillCheatingGuess(region: Region): Boolean {
  val threeByThrees = region.x * region.y / 9
  return region.presents.sum() <= threeByThrees
}

private data class Region(val x: Int, val y: Int, val presents: List<Int>)

private fun parseRegion(str: String): Region {
  val (dims, needs) = str.split(": ")
  val (x, y) = dims.split("x")
  return Region(x.toInt(), y.toInt(), needs.split(" ").map { it.toInt() })
}
