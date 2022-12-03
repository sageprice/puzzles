package adventofcode.`2022`.`03`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/03/input.txt")
    .readLines()

  // Honestly this feels easier than doing ASCII math.
  val key = "0abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

  // Part 1
  var s1 = 0
  for (line in input) {
    val common = line.substring(0, line.length / 2).toSet()
      .intersect(line.substring(line.length / 2).toSet())
      .first()
    s1+= key.indexOf(common)
  }
  println(s1)

  // Part 2
  var s2 = 0
  for (i in 0 until (input.size / 3)) {
    val common = input[3 * i].toSet()
      .intersect(input[3 * i + 1].toSet())
      .intersect(input[3 * i + 2].toSet())
    if (common.size > 1) error("Too many shared: $common")
    s2 += key.indexOf(common.first())
  }
  println(s2)
}