package adventofcode.`2024`.`03`

import java.io.File

/** https://adventofcode.com/2024/day/3 */
fun main() {
  val input = File("src/adventofcode/2024/03/input.txt").readLines().joinToString("")
  println(getMulSum(input))

  val doInstructions = input.split("do()").joinToString("") { it.split("don't()").first() }
  println(getMulSum(doInstructions))
}

private val MUL_REGEX = Regex(
  "mul\\((?<x>\\d+),(?<y>\\d+)\\)"
)

private fun getMulSum(str: String): Long {
  val matches = MUL_REGEX.findAll(str)
  var sum = 0L
  matches.forEach { m ->
    val x = m.groups["x"]?.value?.toLong()
    val y = m.groups["y"]?.value?.toLong()
    if (x != null && y != null) {
      sum += x * y
    }
  }
  return sum
}
