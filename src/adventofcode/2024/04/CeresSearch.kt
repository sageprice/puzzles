package adventofcode.`2024`.`04`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2024/04/input.txt").readLines()
  println(findString(input, "XMAS"))
  println(findCrossedString(input, "MAS"))
}

private fun findString(grid: List<String>, str: String): Long {
  var found = 0L
  for (r in grid.indices) for (c in grid.first().indices) {
    for (dr in -1..1) for (dc in -1..1) {
      // Ya gotta go somewhere
      if (dr == 0 && dc == 0) continue
      if (getDirectionalString(grid, str, r, c, dr, dc) == str) found++
    }
  }
  return found
}

private fun getDirectionalString(grid: List<String>, str: String, r: Int, c: Int, dr: Int, dc: Int): String? {
  // Make sure we stay in the grid...
  if (r + (str.length - 1) * dr !in grid.indices) return null
  if (c + (str.length - 1) * dc !in grid.first().indices) return null
  return str.indices.map { i ->
    grid[r + dr * i][c + dc * i]
  }.joinToString("")
}

private fun findCrossedString(grid: List<String>, str: String): Long {
  var found = 0L
  for (r in grid.indices) for (c in grid.first().indices) {
    if (countCrossedStrings(grid, str, r, c) == 2) found++
  }
  return found
}

// Warning: not a generic solution. Assumes str is length 3 for simplicity.
private fun countCrossedStrings(grid: List<String>, str: String, r: Int, c: Int): Int {
  if (r == 0 || r+1 == grid.size || c == 0 || c+1 == grid.size) return 0
  val downRight = listOf(grid[r-1][c-1], grid[r][c], grid[r+1][c+1]).joinToString("")
  val downLeft = listOf(grid[r+1][c-1], grid[r][c], grid[r-1][c+1]).joinToString("")
  val candidates = listOf(downRight, downLeft, downRight.reversed(), downLeft.reversed())
  return candidates.count { it == str }
}
