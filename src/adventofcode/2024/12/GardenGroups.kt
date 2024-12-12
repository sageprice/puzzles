package adventofcode.`2024`.`12`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2024/12/input.txt").readLines()
  val groups = getGroups(input)
  println(groups.sumOf { it.size * getPlotPerimeter(it) })
  println(groups.sumOf { it.size * countCorners(it) })
}

private fun countCorners(plot: Set<Pair<Int, Int>>): Int {
  // The number of corners is the same as the number of sides.
  var corners = 0
  for ((r, c) in plot) {
    val left = r to c-1
    val right = r to c+1
    val up = r-1 to c
    val down = r+1 to c
    val dl = r+1 to c-1
    val dr = r+1 to c+1
    val ul = r-1 to c-1
    val ur = r-1 to c+1
    // External corners
    if (up !in plot && left !in plot) corners++
    if (up !in plot && right !in plot) corners++
    if (down !in plot && left !in plot) corners++
    if (down !in plot && right !in plot) corners++
    // Internal corners
    if (up in plot && left in plot && ul !in plot) corners++
    if (up in plot && right in plot && ur !in plot) corners++
    if (down in plot && left in plot && dl !in plot) corners++
    if (down in plot && right in plot && dr !in plot) corners++
  }
  return corners
}

private fun getPlotPerimeter(plot: Set<Pair<Int, Int>>): Int =
  plot.sumOf { plant -> plant.adjacent().count { it !in plot} }

private fun getGroups(garden: List<String>): List<Set<Pair<Int, Int>>> {
  val points = garden.indices.flatMap { r -> garden[r].indices.map { c -> r to c } }.toMutableSet()
  val plots = mutableListOf<Set<Pair<Int, Int>>>()
  while (points.isNotEmpty()) {
    val start = points.first()
    points.remove(start)
    val plot = getPlot(start, garden)
    plots.add(plot)
    points.removeAll(plot)
  }
  return plots
}

private fun getPlot(start: Pair<Int, Int>, garden: List<String>): Set<Pair<Int, Int>> {
  val plant = garden[start.first][start.second]
  val plot = mutableSetOf<Pair<Int, Int>>()
  val q = mutableListOf(start)
  while (q.isNotEmpty()) {
    val next = q.removeFirst()
    if (next in plot) continue
    plot.add(next)
    q.addAll(next.adjacent().filter { pair ->
      pair.first in garden.indices &&
          pair.second in garden[pair.first].indices &&
          pair !in plot &&
          garden[pair.first][pair.second] == plant
    })
  }
  return plot
}

private fun Pair<Int, Int>.adjacent(): List<Pair<Int, Int>> = listOf(
  this.first - 1 to this.second,
  this.first + 1 to this.second,
  this.first to this.second - 1,
  this.first to this.second + 1
)
