package adventofcode.`2023`.`06`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2023/06/input.txt").readLines()
  val (times, distances) = input.map { line ->
    line.split(":")
      .last()
      .split(" ")
      .filter { it.isNotEmpty() }
      .map { it.toInt() } }

  println(
    times.zip(distances).map { (time, distance) ->
      (1 until time).count { it * (time - it) > distance }.toLong()
    }.reduce { a, b -> a * b })

  // Part 2
  val (time, distance) = input.map { line ->
    line.split(":")
      .last()
      .filter { it.isDigit() }
      .toLong()
  }
  println((1 until time).count { it * (time - it) > distance })
}