package adventofcode.`2020`.`17`

import java.io.File

fun main() {
  val input: List<List<Char>> =
      File("src/adventofcode/2020/17/input.txt")
          .readLines()
          .map { it.toList() }

  val pocketStart = asMap(input)
  val startDimensions = Dimensions(input[0].indices, input.indices, 0..0, 0..0)

  // Part 1
  var pocket = pocketStart
  var dimensions = startDimensions
  repeat(6) {
    val next = step(pocket, dimensions)
    pocket = next.first
    dimensions = next.second
  }
  println("Part 1: " + countActive(pocket))

  // Part 2
  pocket = pocketStart
  dimensions = startDimensions
  repeat(6) {
    val next = step(pocket, dimensions, inFourSpace = true)
    pocket = next.first
    dimensions = next.second
  }
  println("Part 2: " + countActive(pocket))
}

private fun countActive(pocket: Map<Coord, Boolean>): Int = pocket.values.count { it }

data class Coord(val x: Int, val y: Int, val z: Int, val w: Int)
private fun Coord.surroundings(inFourSpace: Boolean = false): List<Coord> {
  val neighbors = mutableListOf<Coord>()
  for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
    for (dw in (if (inFourSpace) -1..1 else 0..0)) {
      if (dx != 0 || dy != 0 || dz != 0 || dw != 0) {
        neighbors.add(Coord(x+dx, y+dy, z+dz, w + dw))
      }
    }
  }
  return neighbors
}

data class Dimensions(val xs: IntRange, val ys: IntRange, val zs: IntRange, val ws: IntRange)
private fun Dimensions.next(inFourSpace: Boolean = false): Dimensions {
  return Dimensions(
      xs.first-1..xs.last+1,
      ys.first-1..ys.last+1,
      zs.first-1..zs.last+1,
          if (inFourSpace) ws.first-1..ws.last+1 else 0..0)
}

private fun asMap(input: List<List<Char>>): Map<Coord, Boolean> {
  val out = mutableMapOf<Coord, Boolean>()
  for (y in input.indices) {
    for (x in input[0].indices) {
      out[Coord(x, y, 0, 0)] = input[y][x] == '#'
    }
  }
  return out
}

private fun step(
    before: Map<Coord, Boolean>, dimensions: Dimensions, inFourSpace: Boolean = false
): Pair<Map<Coord, Boolean>, Dimensions> {
  val afterCounts = mutableMapOf<Coord, Int>()
  val newDimensions = dimensions.next(inFourSpace)
  for (x in newDimensions.xs) for (y in newDimensions.ys) {
    for (z in newDimensions.zs) for (w in newDimensions.ws) {
      afterCounts[Coord(x, y, z, w)] = 0
    }
  }
  for (coord in before.keys) {
    if (before[coord]!!) {
      coord.surroundings(inFourSpace).forEach {
        if (!afterCounts.containsKey(it)) error("Invalid coord: $it")
        afterCounts[it] = 1 + (afterCounts[it]!!) }
    }
  }
  val after = afterCounts.mapValues { (k, v) ->
    if (k in before.keys) {
      (before[k]!! && v in 2..3) || (!before[k]!! && v == 3)
    } else {
      v == 3
    }
  }
  return Pair(after, newDimensions)
}