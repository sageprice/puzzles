package adventofcode.`2022`.`06`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/06/input.txt").readText()
  for (i in 4 .. input.length) {
    if (input.substring(i-4, i).toSet().size == 4) {
      println(i)
      break
    }
  }
  for (i in 14 .. input.length) {
    if (input.substring(i-14, i).toSet().size == 14) {
      println(i)
      break
    }
  }
}