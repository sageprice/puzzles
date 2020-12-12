package adventofcode.`2020`.`12`

import java.io.File
import kotlin.math.abs

fun main() {
  val directions: List<Direction> =
    File("src/adventofcode/2020/12/input.txt")
      .readLines()
      .map { toDirection(it) }

  println("Part 1: " + navigate(directions).manhattanDistance())
  println("Part 2: " + waypointNavigate(directions).manhattanDistance())
}

private fun navigate(directions: List<Direction>): Ship {
  val ship = Ship(orientation = Orientation.EAST)
  for (direction in directions) {
    ship.move(direction)
  }
  return ship
}

private fun waypointNavigate(directions: List<Direction>): Ship {
  val ship = Ship(orientation = Orientation.EAST)
  for (direction in directions) {
    ship.waypointMove(direction)
  }
  return ship
}

data class Ship(
  var x: Int = 0,
  var y: Int = 0,
  var orientation: Orientation,
  var wayX: Int = 10,
  var wayY: Int = 1)

private fun Ship.move(direction: Direction) {
  when (direction) {
    is Cardinal -> {
      x += direction.orientation.dx * direction.d
      y += direction.orientation.dy * direction.d
    }
    is Forward -> {
      x += orientation.dx * direction.d
      y += orientation.dy * direction.d
    }
    is Turn -> orientation = orientation.turn(direction.angle)
  }
}

private fun Ship.waypointMove(direction: Direction) {
  when(direction) {
    is Cardinal -> {
      wayX += direction.orientation.dx * direction.d
      wayY += direction.orientation.dy * direction.d
    }
    is Forward -> {
      x += wayX * direction.d
      y += wayY * direction.d
    }
    is Turn -> {
      when (direction.angle) {
        1 -> {
          val t = wayX
          wayX = -wayY
          wayY = t
        } // 1,2 -> -2,1 -> -1,-2 -> 2,-1
        2 -> { wayX = -wayX; wayY = -wayY}
        3 -> {
          val t = wayX
          wayX = wayY
          wayY = -t
        } // 1,2 -> 2,-1 -> -1,-2 -> -2,1
      }
    }
  }
}

private fun Ship.manhattanDistance(): Int {
  return abs(x) + abs(y)
}

enum class Orientation(val num: Int, val dx: Int, val dy: Int) {
  EAST(0, 1, 0),
  NORTH(1, 0, 1),
  WEST(2, -1, 0),
  SOUTH(3, 0, -1),
}

private fun Orientation.turn(steps: Int): Orientation {
  return when ((num + steps) % 4) {
    0 -> Orientation.EAST
    1 -> Orientation.NORTH
    2 -> Orientation.WEST
    3 -> Orientation.SOUTH
    else -> error("That's not how math works! $this, $steps")
  }
}

fun toDirection(str: String): Direction {
  val type = str[0]
  val d = str.substring(1).toInt()

  return when (type) {
    'N' -> Cardinal(d, Orientation.NORTH)
    'S' -> Cardinal(d, Orientation.SOUTH)
    'E' -> Cardinal(d, Orientation.EAST)
    'W' -> Cardinal(d, Orientation.WEST)
    'F' -> Forward(d)
    'L' -> Turn(d / 90)
    'R' -> Turn((360 - d) / 90)
    else -> error("Invalid instruction $str")
  }
}

sealed class Direction
data class Turn(val angle: Int): Direction()
data class Forward(val d: Int): Direction()
data class Cardinal(val d: Int, val orientation: Orientation): Direction()