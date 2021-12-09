package adventofcode.`2021`.`09`

import java.io.File

fun main() {

  val heights: List<List<Int>> =
    File("src/adventofcode/2021/09/input.txt")
      .readLines()
      .map {
        it.map { x -> x.toString().toInt() } }

  // Part 1
  val lows = getLows(heights)
  println(lows.map { heights[it.x][it.y].toLong() + 1 }.sum())

  // Part 2
  println(
    lows.map { getBasinSize(it, heights) }
      .sortedDescending()
      .take(3)
      .reduce { a, b -> a * b })
}

private fun getBasinSize(low: Coordinate, cave: List<List<Int>>): Long {
  val pointsToVisit = mutableListOf(low)
  val pointsVisited = mutableSetOf<Coordinate>()
  var countInBasin = 0
  while (pointsToVisit.isNotEmpty()) {
    val next = pointsToVisit.removeAt(0)
    if (cave[next.x][next.y] != 9 && !pointsVisited.contains(next)) {
      countInBasin++
      pointsVisited.add(next)
      getNeighbors(next.x, next.y, cave).forEach {
        pointsToVisit.add(it)
      }
    }
  }
  return countInBasin.toLong()
}

private fun getLows(cave: List<List<Int>>): List<Coordinate> {
  val lows = mutableListOf<Coordinate>()
  for (i in cave.indices) {
    for (j in cave[i].indices) {
      if (isLocalMin(i, j, cave)) {
        lows.add(Coordinate(i, j))
      }
    }
  }
  return lows
}

private fun isLocalMin(x: Int, y: Int, cave: List<List<Int>>): Boolean {
  val p = cave[x][y]
  return getNeighbors(x, y, cave).all { cave[it.x][it.y] > p }
}

private fun getNeighbors(x: Int, y: Int, cave: List<List<Int>>): List<Coordinate> {
  val neighbors = mutableListOf<Coordinate>()
  if (x > 0) neighbors.add(Coordinate(x-1, y))
  if (x < cave.size-1) neighbors.add(Coordinate(x+1, y))
  if (y > 0) neighbors.add(Coordinate(x, y-1))
  if (y < cave[x].size-1) neighbors.add(Coordinate(x, y+1))
  return neighbors
}

private data class Coordinate(val x: Int, val y: Int)