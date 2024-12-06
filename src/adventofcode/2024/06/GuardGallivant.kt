package adventofcode.`2024`.`06`

import adventofcode.`2024`.`06`.Spot.*
import java.io.File

/** https://adventofcode.com/2024/day/6 */
fun main() {
  val grid = File("src/adventofcode/2024/06/input.txt").readLines().map { string ->
    string.map { c ->
      when (c) {
        '#' -> WALL
        '^' -> SEEN // Assumes guard starts oriented upwards.
        else -> UNSEEN
      }
    }
  }
  val (r0, c0) = getStart(grid)
  // Part 1
  val (traversed, _) = walkGuard(grid, r0, c0)
  println(traversed.sumOf { l -> l.count { it == SEEN } })
  // Part 2
  val potentialBlocks = getPotentialBlockSpots(traversed) - (r0 to c0)
  println(getValidBlocks(grid, r0, c0, potentialBlocks).size)
}

private fun getStart(grid: List<List<Spot>>): Pair<Int, Int> {
  for (r in grid.indices) for (c in grid.first().indices) {
    if (grid[r][c] == SEEN) return r to c
  }
  throw IllegalStateException("Could not find starting point")
}

/** Returns the fully-walked grid and a boolean indicating whether the guard looped or walked out. */
private fun walkGuard(grid: List<List<Spot>>, r0: Int, c0: Int): Pair<List<List<Spot>>, Boolean> {
  val traversed = grid.map { it.toTypedArray() }.toTypedArray()
  var (r, c) = r0 to c0
  var (dr, dc) = -1 to 0
  val stepped = mutableSetOf<PosAndDir>()
  var isLoop = true
  while (PosAndDir(r, c, dr, dc) !in stepped) {
    stepped.add(PosAndDir(r, c, dr, dc))
    if (r+dr !in grid.indices || c+dc !in grid[r].indices) {
      isLoop = false
      break
    }
    when (traversed[r+dr][c+dc]) {
      SEEN -> { r += dr; c += dc }
      WALL -> {
        val t = dr
        dr = dc
        dc = -t
      }
      UNSEEN -> {
        r += dr
        c += dc
        traversed[r][c] = SEEN
      }
    }
  }
  return traversed.map { it.toList() }.toList() to isLoop
}

private fun getPotentialBlockSpots(grid: List<List<Spot>>): List<Pair<Int, Int>> {
  val spots = mutableListOf<Pair<Int, Int>>()
  for (r in grid.indices) for (c in grid[r].indices) {
    if (grid[r][c] == SEEN) spots.add(r to c)
  }
  return spots
}

private fun getValidBlocks(
  grid: List<List<Spot>>, r0: Int, c0: Int, blocks: List<Pair<Int, Int>>
): List<Pair<Int, Int>> {
  return blocks.filter { (rb, cb) -> isValidBlock(grid, r0, c0, rb, cb) }
}

private fun isValidBlock(
  grid: List<List<Spot>>, r0: Int, c0: Int, rb: Int, cb: Int
): Boolean {
  val traversed = grid.map { it.toTypedArray() }.toTypedArray()
  traversed[rb][cb] = WALL
  return walkGuard(traversed.map { it.toList() }.toList(), r0, c0).second
}

private data class PosAndDir(val r: Int, val c: Int, val dr: Int, val dc: Int)

private enum class Spot {
  WALL,
  SEEN,
  UNSEEN
}
