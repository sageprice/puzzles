package adventofcode.`2025`.`07`

import java.io.File

/** https://adventofcode.com/2025/day/7 */
fun main() {
  val input = File("src/adventofcode/2025/07/input.txt").readLines()

  val (splits, tachyonPaths) = propagateTachyonsToEnd(input)
  println(splits)
  println(tachyonPaths)
}

private fun propagateTachyonsToEnd(grid: List<String>): Pair<Int, Long> {
  var totalSplits = 0
  var tachyons = List(grid.first().length) { 0L }
  for (r in grid.indices) {
    val (newTachyons, rowSplits) = propagateTachyons(grid, r, tachyons)
    totalSplits += rowSplits
    tachyons = newTachyons
  }
  return totalSplits to tachyons.sum()
}

private fun propagateTachyons(grid: List<String>, row: Int = 0, tachyons: List<Long> = emptyList()): Pair<List<Long>, Int> {
  if (row == 0) {
    return grid.first().map { if (it == 'S') 1L else 0 } to 0
  }
  var splits = 0
  val newTachyons = MutableList(grid[row].length) { 0L }
  tachyons.forEachIndexed { c, ts ->
    if (grid[row][c] == '.') {
      newTachyons[c] += ts
    } else if (grid[row][c] == '^') {
      if (tachyons[c] > 0) {
        splits++
      }
      if (c > 0) {
        newTachyons[c-1] += ts
      }
      if (c < grid.size - 1) {
        newTachyons[c+1] += ts
      }
    } else {
      throw IllegalStateException("hold up: [$row, $c], ${grid[row][c]}")
    }
  }
  return newTachyons to splits
}
