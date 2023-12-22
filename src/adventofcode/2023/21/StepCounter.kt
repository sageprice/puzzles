package adventofcode.`2023`.`21`

import java.io.File

fun main()  {
  val garden = File("src/adventofcode/2023/21/input.txt").readLines()

  println("Part 1: " + walkNSteps(garden, 64).size)
  println("Part 2: " + extrapolateToNSteps(garden, 26501365))
}

private fun extrapolateToNSteps(garden: List<String>, n: Int): Long {
  val parity = n % garden.size
  val startRow = garden.indices.first { r -> garden[r].contains('S') }
  val startCol = garden[startRow].indexOf('S')
  var locs = listOf(Pair(startRow, startCol))
  val dataPoints = mutableListOf<Pair<Int, Long>>()
  var i = 0
  while (dataPoints.size < 3) {
    i++
    locs = locs.map { (r, c) -> takeStep(r, c, garden) }.flatten().distinct()
    if (i % garden.size == parity) {
      dataPoints.add(Pair(i / garden.size, locs.size.toLong()))
      println("After $i steps, we can be in any of ${locs.size} locations")
    }
  }
  val x0 = dataPoints.first().second
  // Solve the quadratic equation with the latter two points to get the coefficients.
  val x1 = (4 * dataPoints[1].second - dataPoints[2].second - 3 * x0) / 2
  val x2 = (dataPoints[2].second - 2 * dataPoints[1].second + x0) / 2
  println("y = $x0 + $x1*x + $x2*x^2")
  return x0 + x1 * (n / garden.size) + x2 * (n / garden.size) * (n / garden.size)
}

private fun walkNSteps(garden: List<String>, n: Int): List<Pair<Int, Int>> {
  val startRow = garden.indices.first { r -> garden[r].contains('S') }
  val startCol = garden[startRow].indexOf('S')
  var locs = listOf(Pair(startRow, startCol))
  repeat(n) { locs = locs.map { (r, c) -> takeStep(r, c, garden) }.flatten().distinct() }
  return locs
}

private fun takeStep(r: Int, c: Int, garden: List<String>): List<Pair<Int, Int>> {
  val next = listOf(Pair(r, c + 1), Pair(r + 1, c), Pair(r, c - 1), Pair(r - 1, c))
  return next.filter { (r, c) ->
    garden[(r + (garden.size * 100)) % garden.size][
        (c + garden.first().length * 100) % garden.first().length] != '#'
  }
}

private fun printGarden(points: List<Pair<Int, Int>>, garden: List<String>) {
  for (r in garden.indices) {
    for (c in garden[r].indices) {
      if (Pair(r, c) in points) {
        print("O")
      } else {
        print(garden[r][c])
      }
    }
    println()
  }
}
