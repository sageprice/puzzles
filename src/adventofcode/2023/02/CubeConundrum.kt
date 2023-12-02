package adventofcode.`2023`.`02`

import java.io.File

fun main() {
  val lines: List<Game> =
    File("src/adventofcode/2023/02/input.txt")
      .readLines()
      .map { parseGame(it) }

  // Part 1
  println(lines.filter { isPossiblePart1Game(it) }.sumOf { it.id })

  // Part 2
  println(lines.sumOf { getGamePower(it) })
}

private data class Draw(
  val x: Int,
  val color: String)

private data class Game(
  val id: Int,
  val rounds: List<List<Draw>>)

private fun parseGame(str: String): Game {
  val (id, drawn) = str.split(": ")
  val separateDraws = drawn.split("; ")
  val rounds =
    separateDraws.map { it.split(", ")
      .map { singleDraw ->
        val (count, color) = singleDraw.split(" ")
        Draw(x = count.toInt(), color) }
    }
  return Game(id.split(" ").last().toInt(), rounds)
}

private fun isPossiblePart1Game(g: Game): Boolean {
  return g.rounds.all { draws ->
    draws.all { d ->
      when (d.color) {
        "red" -> d.x <= 12
        "green" -> d.x <= 13
        "blue" -> d.x <= 14
        else -> true
      }
    }
  }
}

private fun getGamePower(g: Game): Long {
  return g.rounds
    .flatten()
    .groupBy { it.color }
    .values
    .map { counts -> counts.maxOf { it.x }.toLong() }
    .reduce { a, b -> a * b }
}