package adventofcode.`2021`.`02`

import java.io.File

fun main() {
  val moves: List<Move> =
    File("src/adventofcode/2021/02/input.txt")
      .readLines()
      .map { it.split(" ") }
      .map { Move(it.first(), it[1].toInt()) }

  var x = 0L
  var y = 0L
  for (move in moves) {
    when (move.direction) {
      "forward" -> x += move.length
      "up" -> y -= move.length
      "down" -> y += move.length
      else -> error("Unexpected move: $move")
    }
  }
  println(x*y)

  x = 0
  y = 0
  var aim = 0
  for (move in moves) {
    when (move.direction) {
      "forward" -> {
        x += move.length
        y += move.length * aim
      }
      "up" -> aim -= move.length
      "down" -> aim += move.length
      else -> error("Unexpected move: $move")
    }
  }
  println(x*y)
}

private data class Move(val direction: String, val length: Int)