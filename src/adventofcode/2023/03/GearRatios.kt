package adventofcode.`2023`.`03`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val lines = File("src/adventofcode/2023/03/input.txt").readLines()

  // Part 1
  val (total, indexedPartNumbers) = getPartNumbers(lines)
  println(total)

  // Part 2
  println(calculateGearRatioSum(lines, indexedPartNumbers))
}

private fun calculateGearRatioSum(
  lines: List<String>,
  indexedPartNumbers: Map<Pair<Int, Int>, List<Int>>): Long {
  var gearRatioSum = 0L
  for (i in lines.indices) for (j in lines[i].indices) {
    if (lines[i][j] == '*') {
      val adjacentGears = indexedPartNumbers[Pair(i, j)]
      if (adjacentGears?.size == 2) {
        gearRatioSum += adjacentGears.first() * adjacentGears.last()
      }
    }
  }
  return gearRatioSum
}

/**
 * Returns the sum of all numbers found in the list, along with a map indexed by coordinates adjacent to and containing
 * the located numbers.
 */
private fun getPartNumbers(lines: List<String>): Pair<Int, Map<Pair<Int, Int>, List<Int>>> {
  val locatedParts = mutableMapOf<Pair<Int, Int>, List<Int>>()
  var total = 0
  for (r in lines.indices) {
    var c = 0
    while (c < lines[r].length) {
      if (lines[r][c] in '0'..'9') {
        val numLen = getNumberLength(lines[r], c)
        if (isAdjacentToSymbol(lines, r, c, numLen)) { // TODO: could move this out as a final scan. No need to filter.
          val num = lines[r].substring(c, c + numLen).toInt()
          for (i in r-1..r+1) for (j in c-1..c+numLen) {
            val p = Pair(i, j)
            locatedParts[p] = (locatedParts[p] ?: emptyList()) + num
          }
          total += num
        }
        c += numLen
      } else c++
    }
  }
  return Pair(total, locatedParts)
}

private fun getNumberLength(s: String, start: Int): Int {
  var end = start + 1
  while (end < s.length && s[end] in '0'..'9') end++
  return end - start
}

private fun isAdjacentToSymbol(s: List<String>, r: Int, c: Int, len: Int): Boolean {
  for (row in max(0, r-1)..min(s.size-1, r+1)) {
    for (col in max(0, c-1)..min(s[r].length-1, c + len)) {
      if (s[row][col] != '.' && s[row][col] !in '0'..'9') return true
    }
  }
  return false
}