package adventofcode.`2024`.`14`

import java.io.File
import kotlin.math.sqrt

/** https://adventofcode.com/2024/day/14 */
fun main() {
  val robots = File("src/adventofcode/2024/14/input.txt").readLines().map { parseRobot(it) }
  val w = 101
  val h = 103
  // Part 1
  var moved = robots.toList()
  repeat(100) {
    moved = getNextPositions(moved, w, h)
  }
  println(getQuadrantScores(moved, w, h).reduce { a, b -> a*b })

  // Part 2
  moved = robots.toList()
  var i = 0
  val stdDevs = mutableListOf<Pair<Double, Double>>()
  while (true) {
    moved = getNextPositions(moved, w, h)
    i++
    // Checking for an NxN block is sufficient, but slow. One good filter is to check that the x and y coords are
    // clustered closely together based on their standard deviation, saving us the O(X*Y*N^2) check for an NxN block.
    val (rStdDev, cStdDev) = stdDevs(moved)
    if (stdDevs.size > 100
      && rStdDev < .7 * stdDevs.map { it.first }.average()
      && cStdDev < .7 * stdDevs.map { it.second }.average()
      && hasNxN(moved, w, h, n = 3)) {
      println(i)
      break
    }
    stdDevs.add(rStdDev to cStdDev)
  }
}

private fun stdDevs(robots: List<Robot>): Pair<Double, Double> {
  return stdDev(robots.map { it.r }) to stdDev(robots.map { it.c })
}

private fun stdDev(xs: List<Int>): Double {
  val avg = xs.average()
  val sumSquareDevs = xs.sumOf { x -> (x-avg)*(x-avg) }
  return sqrt(sumSquareDevs / xs.size)
}

private fun hasNxN(robots: List<Robot>, w: Int, h: Int, n: Int): Boolean {
  val rset = robots.map { it.r to it.c }.toSet()
  for (r in 0 until h-n) {
    for (c in 0 until w-n) {
      val box = (c until c+n).flatMap { c0 -> (r until r+n).map { r0 -> r0 to c0 } }
      if (box.all { it in rset }) {
        return true
      }
    }
  }
  return false
}

private fun getQuadrantScores(robots: List<Robot>, w: Int, h: Int): List<Int> {
  val (top, bottom) = robots.filter { it.r != h / 2 && it.c != w / 2 }.partition { it.r < h / 2 }
  val (tl, tr) = top.partition { it.c < w / 2 }
  val (bl, br) = bottom.partition { it.c < w / 2 }
  return listOf(tl.size, tr.size, bl.size, br.size)
}

/** Sanity helper for debugging. */
private fun printRobots(robots: List<Robot>, w: Int, h: Int) {
  val grouped = robots.groupBy { it.r to it.c }.mapValues { it.value.size }
  for (r in 0 until h) {
    for (c in 0 until w) {
      print(grouped[r to c] ?: " ")
    }
    println()
  }
}

private fun getNextPositions(robots: List<Robot>, width: Int, height: Int): List<Robot> {
  return robots.map { it.next(width, height) }
}

private fun parseRobot(line: String): Robot {
  val (ps, ds) = line.split(" ").map { it.split("=").last() }
  val (c, r) = ps.split(",").map { it.toInt() }
  val (dc, dr) = ds.split(",").map { it.toInt() }
  return Robot(r, c, dr, dc)
}

private data class Robot(
  val r: Int, val c: Int, val dr: Int, val dc: Int
) {
  fun next(width: Int, height: Int): Robot {
    return Robot(
      (r + dr + height) % height,
      (c + dc + width) % width,
      dr,
      dc)
  }
}