package adventofcode.`2021`.`05`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val lines: List<List<Point>> =
    File("src/adventofcode/2021/05/input.txt")
      .readLines()
      .map { line ->
        val nums = line.split(" -> ", ",").map { it.toInt() }
        listOf(Point(nums[0], nums[1]), Point(nums[2], nums[3]))
      }

  // Part 1
  val xMax = lines.maxOf { max(it.first().x, it.last().x) }
  val yMax = lines.maxOf { max(it.first().y, it.last().y) }
  val grid = Array(yMax+1) { IntArray(xMax+1) }

  for (line in lines) {
    val (p1, p2) = line
    if (p1.x == p2.x) {
      val x = p1.x
      val miny = min(p1.y, p2.y)
      val maxy = max(p1.y, p2.y)
      for (y in miny..maxy) {
        grid[y][x]++
      }
    } else if (p1.y == p2.y) {
      val y = p1.y
      val minx = min(p1.x, p2.x)
      val maxx = max(p1.x, p2.x)
      for (x in minx..maxx) {
        grid[y][x]++
      }
    }
  }
  println(grid.map { it.count { hits -> hits >= 2 } }.sum())

  // Part 2
  val diagrid = Array(yMax+1) { IntArray(xMax+1) }
  for (line in lines) {
    var (p1: Point, p2: Point) = line
    val minx = min(p1.x, p2.x)
    val maxx = max(p1.x, p2.x)
    val miny = min(p1.y, p2.y)
    val maxy = max(p1.y, p2.y)
    when {
      minx == maxx -> {
        for (y in miny..maxy) diagrid[y][minx]++
      }
      miny == maxy -> {
        for (x in minx..maxx) diagrid[miny][x]++
      }
      else -> {
        if (p1.x > p2.x) {
          val t = p1
          p1 = p2
          p2 = t
        }
        (p1.x..p2.x)
          .zip(if (p1.y < p2.y) p1.y..p2.y else p1.y downTo p2.y)
          .forEach { (x,y) -> diagrid[y][x]++ }
      }
    }
//    // Debugging
//    println(line)
//    for (row in diagrid) {
//      println(row.joinToString(","))
//    }
  }
  println(diagrid.map { it.count { hits -> hits >= 2 } }.sum())
}

private data class Point(val x: Int, val y: Int)