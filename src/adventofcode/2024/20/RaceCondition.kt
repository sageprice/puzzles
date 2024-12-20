package adventofcode.`2024`.`20`

import java.io.File
import kotlin.math.abs

/** https://adventofcode.com/2024/day/20 */
fun main() {
  val grid = File("src/adventofcode/2024/20/input.txt").readLines()

  // Part 1
  val distances = getWalkDistance(grid)
  val shortcuts = getShortcuts(distances, 2)
  println(shortcuts.groupBy { it }.mapValues { (d, l) -> l.size }.filter { it.key >= 100 }.toList().sumOf { it.second })
  // Part 2
  val shortcuts2 = getShortcuts(distances, 20)
  println(shortcuts2.groupBy { it }.mapValues { (d, l) -> l.size }.filter { it.key >= 100 }.toList().sumOf { it.second })
}

private fun getShortcuts(distances: Map<Pair<Int, Int>, Int>, dMax: Int = 2): List<Int> {
  val shortcuts = mutableListOf<Int>()
  for ((r0, c0) in distances.keys) {
    val cutsFromHere = distances.keys.filter { (r1, c1) -> abs(r1-r0) + abs(c1-c0) <= dMax }.map { (r1, c1) ->
      (distances[r1 to c1] ?: throw IllegalArgumentException("[$r1, $c1] not a valid location")) -
          (distances[r0 to c0] ?: throw IllegalArgumentException("[$r0, $c0] not a valid location")) -
          (abs(r1-r0) + abs(c1-c0))
    }.filter { it > 0 }
    shortcuts.addAll(cutsFromHere)
  }
  return shortcuts
}

private fun getWalkDistance(grid: List<String>): Map<Pair<Int, Int>, Int> {
  val distances = mutableMapOf<Pair<Int, Int>, Int>()
  val start = findPosOf('S', grid)
  val end = findPosOf('E', grid)
  var d = 0
  var next = start
  while (next != end) {
    distances[next] = d++
    val (r, c) = next
    next = listOf(r-1 to c, r+1 to c, r to c-1, r to c+1)
      .first { (a, b) -> (grid[a][b] == '.' || a to b == end) && a to b !in distances }
  }
  distances[end] = d
  return distances
}

private fun findPosOf(ch: Char, grid: List<String>): Pair<Int, Int> {
  for (r in grid.indices) for (c in grid[r].indices) if (grid[r][c] == ch) return r to c
  throw IllegalStateException("Could not find `$ch` in grid")
}
