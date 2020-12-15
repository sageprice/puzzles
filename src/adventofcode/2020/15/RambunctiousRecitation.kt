package adventofcode.`2020`.`15`

import java.io.File

fun main() {
  val nums: List<Int> =
      File("src/adventofcode/2020/15/input.txt")
          .readLines()[0].split(",").map { it.toInt() }


  println("Part 1: " + countOff(nums, 2020))

  println("Part 2: " + countOff(nums, 30_000_000))
}

private fun countOff(nums: List<Int>, target: Int): Int {
  val lastSpoken: MutableMap<Int, Int> = mutableMapOf()
  nums.forEachIndexed { i, v -> lastSpoken[v] = i + 1 }
  var next = 0
  for (i in lastSpoken.size + 1 until target) {
    val newNext = if (lastSpoken.containsKey(next)) i - lastSpoken[next]!! else 0
    lastSpoken[next] = i
    next = newNext
  }
  return next
}