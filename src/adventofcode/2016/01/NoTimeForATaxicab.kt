package adventofcode.`2016`.`01`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2016/01/input.txt").readText().split(", ")

  val (x, y) = getDistances(input)
  println(x + y)

  val (rx, ry) = findFirstRepeat(input)
  println(rx + ry)
}

private fun getDistances(steps: List<String>): Pair<Int, Int> {
  var dx = 0
  var dy = 1
  var x = 0
  var y = 0
  steps.forEach { step ->
    when(step[0]) {
      'R' -> {
        val t = dx
        dx = dy
        dy = -t
      }
      'L' -> {
        val t = dy
        dy = dx
        dx = -t
      }
    }
    val d = step.substring(1).toInt()
    x += dx*d
    y += dy*d
  }
  return x to y
}

private fun findFirstRepeat(steps: List<String>): Pair<Int, Int> {
  var dx = 0
  var dy = 1
  var x = 0
  var y = 0
  val visited = mutableSetOf(0 to 0)
  steps.forEach { step ->
    when(step[0]) {
      'R' -> {
        val t = dx
        dx = dy
        dy = -t
      }
      'L' -> {
        val t = dy
        dy = dx
        dx = -t
      }
    }
    val d = step.substring(1).toInt()
    for (i in 1..d) {
      x += dx
      y += dy
      if (x to y in visited) return x to y
      else visited.add(x to y)
    }
  }
  throw IllegalStateException("Never revisited anywhere")
}