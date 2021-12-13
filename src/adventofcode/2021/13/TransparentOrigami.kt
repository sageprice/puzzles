package adventofcode.`2021`.`13`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2021/13/input.txt").readText()

  var points = input
    .split("\n\n")
    .first()
    .split("\n")
    .map { it.split(",").map { x -> x.toInt() } }
    .map { (x, y) ->Point(x, y) }.toSet()

  val folds: List<String> = input
    .split("\n\n")
    .last()
    .split("\n")
    .map { it.split(" ").last() }

  // Part 1
  println(makeFold(points, folds.first()).size)

  // Part 2
  for (fold in folds) {
    points = makeFold(points, fold)
  }
  val xMax = points.maxOf { it.x }
  val yMax = points.maxOf { it.y }
  for (y in 0..yMax) {
    for (x in 0..xMax) {
      print(if (points.contains(Point(x, y))) "#" else " ")
    }
    println()
  }
}

private fun makeFold(points: Set<Point>, instr: String): Set<Point> {
  val foldedPoints = mutableSetOf<Point>()
  val (axis, lStr) = instr.split("=")
  val line = lStr.toInt()
  if (axis == "x") {
    for (point in points) {
      if (line < point.x) {
        foldedPoints.add(Point(line - (point.x - line), point.y))
      } else if (line > point.x) {
        foldedPoints.add(point)
      }
    }
  } else {
    for (point in points) {
      if (line < point.y) {
        foldedPoints.add(Point(point.x, line - (point.y - line)))
      } else if (line > point.y) {
        foldedPoints.add(point)
      }
    }
  }
  return foldedPoints
}

private data class Point(val x: Int, val y: Int)