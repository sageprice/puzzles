package adventofcode.`2019`.`03`

import java.io.File
import kotlin.math.abs

/**
 * Solution for [Advent of Code day 3](https://adventofcode.com/2019/day/3).
 */
fun main() {
  // Read input file
  val instructions: List<List<Component>> =
      File("input.txt")
      // File("test_input.txt")
         .readLines()
         .map { it.split(",").map { toComponent(it) } }

  // Part 1
  val firstWireDisplacements = getWirePath(instructions[0])
  val secondWireDisplacements = getWirePath(instructions[1])
  val secondSet = secondWireDisplacements.toSet()
  val connections: List<Point> = firstWireDisplacements.filter { secondSet.contains(it) }
  println("Part 1 answer: ${connections.map { abs(it.x) + abs(it.y) }.filter { it > 0 }.minOrNull()!!}")

  // Part 2
  var dMin = 100000000
  for (connection in connections) {
      var d1: Int = 100000
      var d2: Int = 100000
      for (i in firstWireDisplacements.indices) {
          if (firstWireDisplacements[i] == connection) {
            d1 = i
            break
          }
      }
      for (i in secondWireDisplacements.indices) {
          if (secondWireDisplacements[i] == connection) {
            d2 = i
            break
          }
      }
      if ((d1 + d2 > 0) and (d1 + d2 < dMin)) {
          dMin = d1 + d2
      }
  }
  println("Part 2 answer: $dMin")
}

fun getWirePath(components: List<Component>): List<Point> {
  val sequence: MutableList<Point> = mutableListOf()
  sequence.add(Point(0, 0))
  for (instruction in components) {
      for (i in 0 until instruction.length) {
          val last = sequence.last()
          when (instruction.direction) {
              Direction.UP -> sequence.add(Point(last.x, last.y + 1))
              Direction.DOWN -> sequence.add(Point(last.x, last.y - 1))
              Direction.LEFT -> sequence.add(Point(last.x - 1, last.y))
              Direction.RIGHT -> sequence.add(Point(last.x + 1, last.y))
          }
      }
  }
  return sequence
}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun asDirection(str: String): Direction {
    return when (str) {
        "U" -> Direction.UP
        "D" -> Direction.DOWN
        "L" -> Direction.LEFT
        "R" -> Direction.RIGHT
        else -> throw Exception("That ain't right $str")
    }
}

fun toComponent(step: String): Component {
    return Component(asDirection(step.substring(0, 1)), step.substring(1).toInt())
}

data class Component(val direction: Direction, val length: Int)

data class Point(val x: Int, val y: Int)