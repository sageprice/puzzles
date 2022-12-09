package adventofcode.`2022`.`09`

import java.io.File
import kotlin.math.abs

fun main() {
  val input = File("src/adventofcode/2022/09/input.txt")
    .readLines()
    .map { val (a, b) = it.split(" "); Pair(a, b.toInt()) }

  var rope = List(2) { Pair(0, 0) }
  val part1Visits = mutableSetOf(rope.last())
  input.forEach { (direction, steps) ->
    repeat(steps) {
      rope = dragRope(rope, direction)
      part1Visits.add(rope.last())
    }
  }
  println(part1Visits.size)

  rope = List(10) { Pair(0, 0)}
  val part2Visits = mutableSetOf(Pair(0, 0))
  input.forEach { (direction, steps) ->
    repeat(steps) {
      rope = dragRope(rope, direction)
      part2Visits.add(rope.last())
    }
  }
  println(part2Visits.size)
}

private fun dragRope(rope: List<Pair<Int, Int>>, direction: String): List<Pair<Int, Int>> {
  val r = mutableListOf(moveHead(rope.first(), direction))
  for (i in 1 until rope.size) {
    r.add(moveTail(r.last(), rope[i]))
  }
  return r
}

private fun moveHead(head: Pair<Int, Int>, direction: String): Pair<Int, Int> {
  return when (direction) {
    "U" -> Pair(head.first, head.second + 1)
    "D" -> Pair(head.first, head.second - 1)
    "L" -> Pair(head.first - 1, head.second)
    "R" -> Pair(head.first + 1, head.second)
    else -> error("Invalid input: $direction")
  }
}

private fun moveTail(head: Pair<Int, Int>, tail: Pair<Int, Int>): Pair<Int, Int> {
  val dx = head.first - tail.first
  val dy = head.second - tail.second
  return if (abs(dx) < 2 && abs(dy) < 2) tail else {
    val firstStep = if (dx == 0) 0 else dx / abs(dx)
    val secondStep = if (dy == 0) 0 else dy / abs(dy)
    Pair(tail.first + firstStep, tail.second + secondStep)
  }
}
