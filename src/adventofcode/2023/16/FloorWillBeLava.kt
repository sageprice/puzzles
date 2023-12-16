package adventofcode.`2023`.`16`

import java.io.File

fun main() {
  val mirrorGrid = File("src/adventofcode/2023/16/input.txt").readLines().map { it.toList() }

  // Part 1
  println(
    fireBeams(Beam(0, -1, 0, 1), mirrorGrid)
      .sumOf { row -> row.count { it > 0 } })

  // Part 2
  println(
    getPossibleStarts(mirrorGrid)
      .map { fireBeams(it, mirrorGrid) }
      .maxOf { it.sumOf { row -> row.count { x -> x > 0 } } })
}

private fun getPossibleStarts(grid: List<List<Char>>): List<Beam> {
  val beams = mutableListOf<Beam>()
  for (r in grid.indices) {
    beams.add(Beam(r, -1, 0, 1))
    beams.add(Beam(r, grid[r].size, 0, -1))
  }
  for (c in grid.first().indices) {
    beams.add(Beam(-1, c, 1, 0))
    beams.add(Beam(grid.size, c, -1, 0))
  }
  return beams
}

private fun fireBeams(start: Beam, grid: List<List<Char>>): Array<IntArray> {
  val beams = mutableListOf(start)
  val energized = Array(grid.size) { IntArray(grid.first().size) { 0 } }

  // There are infinite loops, we should stop when we find ourselves in the same place.
  val visited = mutableSetOf<Beam>()
  while (beams.isNotEmpty()) {
    val beam = beams.removeFirstOrNull() ?: error("Why am I here if there aren't BEAMS")
    if (beam in visited) continue
    visited.add(beam)
    if (beam.c in grid.first().indices && beam.r in grid.indices) energized[beam.r][beam.c]++
    beams.addAll(getNextLocation(beam, grid))
  }
  return energized
}

private fun getNextLocation(beam: Beam, grid: List<List<Char>>): List<Beam> {
  val nextR = beam.r + beam.dr
  val nextC = beam.c + beam.dc
  if (nextR !in grid.indices) return emptyList()
  if (nextC !in grid.first().indices) return emptyList()
  return when (grid[nextR][nextC]) {
    '-' -> {
      if (beam.dr == 0) listOf(Beam(nextR, nextC, 0, beam.dc))
      else listOf(
        Beam(nextR, nextC, 0, -1),
        Beam(nextR, nextC, 0, 1)
      )
    }
    '|' -> {
      if (beam.dc == 0) listOf(Beam(nextR, nextC, beam.dr, 0))
      else listOf(
        Beam(nextR, nextC, -1, 0),
        Beam(nextR, nextC, 1, 0)
      )
    }
    '.' -> listOf(Beam(nextR, nextC, beam.dr, beam.dc))
    '\\' -> listOf(Beam(nextR, nextC, beam.dc, beam.dr))
    '/' -> listOf(Beam(nextR, nextC, -beam.dc, -beam.dr))
    else -> error("Where are we? $beam --> $nextR, $nextC")
  }
}

private data class Beam(val r: Int, val c: Int, val dr: Int, val dc: Int)
