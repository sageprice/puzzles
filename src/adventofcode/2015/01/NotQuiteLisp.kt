package adventofcode.`2015`.`01`

import java.io.File

fun main() {
  val chars =
    File("src/adventofcode/2015/01/input.txt")
      .readText().toCharArray()

  // Part 1
  println(chars.count { it == '(' } - chars.count { it == ')' })

  // Part 2
  var floor = 0
  for (i in chars.indices) {
    if (chars[i] == '(') floor++
    else floor--
    if (floor < 0) {
      println(i+1)
      break
    }
  }
}