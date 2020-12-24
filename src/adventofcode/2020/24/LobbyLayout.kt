package adventofcode.`2020`.`24`

import java.io.File

fun main() {
  val lines = File("src/adventofcode/2020/24/input.txt").readLines()

  // Part 1
  val finalTiles = lines.map { parseDirections(it).walk() }
  val tileCounts = mutableMapOf<Tile, Int>()
  finalTiles.forEach { tile ->
    if (tileCounts.contains(tile)) {
      tileCounts[tile] = tileCounts[tile]!! + 1
    } else {
      tileCounts[tile] = 1
    }
  }
  val initialBlackTiles = tileCounts.filterValues { it % 2 == 1 }.keys
  println("Part 1: " + initialBlackTiles.size)

  // Part 2
  var tiles = initialBlackTiles.toSet()
  repeat(100) { tiles = getNextBlackTiles(tiles) }
  println("Part 2: " + tiles.size)
}

typealias Tile = Pair<Int, Int>

private fun getNextBlackTiles(initial: Set<Tile>): Set<Tile> {
  val adjacentBlackTiles = mutableMapOf<Tile, Int>()
  initial.forEach { adjacentBlackTiles[it] = 0 }
  for (tile in initial) {
    Step.values().forEach {
      val adjacentTile = Tile(tile.first + it.dx, tile.second + it.dy)
      if (adjacentBlackTiles.containsKey(adjacentTile)) {
        adjacentBlackTiles[adjacentTile] = adjacentBlackTiles[adjacentTile]!! + 1
      } else {
        adjacentBlackTiles[adjacentTile] = 1
      }
    }
  }
  return adjacentBlackTiles.mapNotNull { (tile, c) ->
    when {
      initial.contains(tile) && c in 1..2 -> tile
      !initial.contains(tile) && c == 2 -> tile
      else -> null
    }
  }.toSet()
}

private fun parseDirections(line: String): Directions {
  val steps = mutableListOf<Step>()
  var i = 0
  while (i < line.length) {
    val step = when (line[i]) {
      'e' -> Step.E
      'w' -> Step.W
      'n' -> {
        i++
        if (line[i] == 'e') Step.NE
        else Step.NW
      }
      's' -> {
        i++
        if (line[i] == 'e') Step.SE
        else Step.SW
      }
      else -> error("Invalid letter: $line, $i, ${line[i]}")
    }
    i++
    steps.add(step)
  }
  return Directions(steps)
}

private fun Directions.walk(): Tile {
  var x = 0
  var y = 0
  for (step in steps) {
    x += step.dx
    y += step.dy
  }
  return Tile(x, y)
}

data class Directions(val steps: List<Step>)
enum class Step(val dx: Int, val dy: Int) {
  E(2,0),
  SE(1, -1),
  SW(-1, -1),
  W(-2, 0),
  NW(-1, 1),
  NE(1, 1),
}
