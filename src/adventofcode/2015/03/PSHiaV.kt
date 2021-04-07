package adventofcode.`2015`.`03`

import java.io.File

fun main() {
  val instrs =
    File("src/adventofcode/2015/03/input.txt")
      .readText()

  // Part 1
  var x = 0
  var y = 0
  val visited = mutableSetOf(Pair(0, 0))
  for (c in instrs) {
    when (c) {
      '<' -> x--
      '>' -> x++
      '^' -> y++
      'v' -> y--
    }
    visited.add(Pair(x,y))
  }
  println(visited.size)

  // Part 2
  x = 0
  y = 0
  var rx = 0
  var ry = 0
  val v2 = mutableSetOf(Pair(0, 0))
  var isSanta = true
  for (c in instrs) {
    when (c) {
      '<' -> if (isSanta) x-- else rx--
      '>' -> if (isSanta) x++ else rx++
      '^' -> if (isSanta) y++ else ry++
      'v' -> if (isSanta) y-- else ry--
    }
    if (isSanta) v2.add(Pair(x, y)) else v2.add(Pair(rx, ry))
    isSanta = !isSanta
  }
  println(v2.size)
}