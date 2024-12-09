package adventofcode.`2024`.`08`

import java.io.File

/** https://adventofcode.com/2024/day/8 */
fun main() {
  val grid = File("src/adventofcode/2024/08/input.txt").readLines()
  val antennas = findMatchingAntennas(grid)
  // Part 1
  println(findAntinodes(antennas, grid.size, grid.first().length, shouldResonate = false).size)
  // Part 2
  println(findAntinodes(antennas, grid.size, grid.first().length, shouldResonate = true).size)
}

private fun findMatchingAntennas(grid: List<String>): Map<Char, List<Pair<Int, Int>>> {
  val antennas = mutableListOf<Pair<Int, Int>>()
  for (r in grid.indices) for (c in grid[r].indices) {
    if (grid[r][c] != '.') antennas.add(r to c)
  }
  return antennas.groupBy { (r, c) -> grid[r][c] }
}

private fun findAntinodes(
  antennas: Map<Char, List<Pair<Int, Int>>>, r: Int, c: Int, shouldResonate: Boolean
): Set<Pair<Int, Int>> {
  val antinodes = mutableSetOf<Pair<Int, Int>>()
  for (antennaList in antennas.values) {
    for (i in antennaList.indices) for (j in i+1 until antennaList.size) {
      antinodes.addAll(findAntinodes(antennaList[i], antennaList[j], r, c, shouldResonate))
    }
  }
  if (shouldResonate) antinodes.addAll(antennas.values.flatten())
  return antinodes
}

private fun findAntinodes(
  a1: Pair<Int, Int>, a2: Pair<Int, Int>, r: Int, c: Int, shouldResonate: Boolean
): List<Pair<Int, Int>> {
  val antinodes = mutableListOf<Pair<Int, Int>>()
  val d = a2 - a1
  var x = a2 + d
  var y = a1 - d
  while (x.first in 0 until r && x.second in 0 until c) {
    antinodes.add(x)
    if (!shouldResonate) break
    x += d
  }
  while (y.first in 0 until r && y.second in 0 until c) {
    antinodes.add(y)
    if (!shouldResonate) break
    y -= d
  }
  return antinodes
}

private infix operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>): Pair<Int, Int> =
  (this.first + other.first) to (this.second + other.second)

private infix operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>): Pair<Int, Int> =
  (this.first - other.first) to (this.second - other.second)
