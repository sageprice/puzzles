package adventofcode.`2021`.`15`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val input: List<List<Int>> = File("src/adventofcode/2021/15/input.txt")
    .readLines()
    .map { it.filter { c -> c.isDigit() }.map { c -> c.toString().toInt() } }

  // Part 1
  val costs = getCosts(input)
  println(costs.last().last() - costs.first().first())

  // Part 2
  val bigCave: MutableList<MutableList<Int>> = input.map { it.toMutableList() }.toMutableList()
  // Expand rows
  for (i in bigCave.indices) repeat(4) { bigCave[i].addAll(input[i]) }
  // Expand columns -- make copy so updates don't propagate
  repeat(4) { for (i in input.indices) bigCave.add(bigCave[i].toMutableList()) }
  for (i in bigCave.indices) for (j in bigCave[i].indices) {
    bigCave[i][j] += (i / input.size) + (j / input.size)
    while (bigCave[i][j] > 9) bigCave[i][j] -= 9
  }
  val bigCosts = getCosts(bigCave)
  println(bigCosts.last().last() - bigCosts.first().first())
}

private fun getCosts(cave: List<List<Int>>): Array<IntArray> {
  val costs = Array(cave.size) { IntArray(cave.first().size) { Int.MAX_VALUE / 3} }
  costs[0][0] = cave[0][0]

  // Use Djikstra's algo. Keep a heap for tracking next point w/ lowest cost.
  val pointComparator = kotlin.Comparator<Point> { a, b -> costs[a.x][a.y] - costs[b.x][b.y] }
  val queue = PriorityQueue(pointComparator)
  queue.add(Point(0, 0))
  while (queue.isNotEmpty()) {
    val low = queue.remove()
    for (neighbor in getNeighbors(low, cave)) {
      if (costs[low.x][low.y] + cave[neighbor.x][neighbor.y] < costs[neighbor.x][neighbor.y]) {
        costs[neighbor.x][neighbor.y] = costs[low.x][low.y] + cave[neighbor.x][neighbor.y]
        queue.add(neighbor)
      }
    }
  }
  return costs
}

private fun getNeighbors(p: Point, xs: List<List<Any>>): List<Point> {
  val neighbors = mutableListOf<Point>()
  if (p.x > 0) neighbors.add(Point(p.x - 1, p.y))
  if (p.y > 0) neighbors.add(Point(p.x, p.y - 1))
  if (p.x < xs.size - 1) neighbors.add(Point(p.x + 1, p.y))
  if (p.y < xs[0].size - 1) neighbors.add(Point(p.x, p.y + 1))
  return neighbors
}

private data class Point(val x: Int, val y: Int)