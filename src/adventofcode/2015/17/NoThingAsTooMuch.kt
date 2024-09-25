package adventofcode.`2015`.`17`

import java.io.File

fun main() {
  val totalEggnog = 150
  val containers = File("src/adventofcode/2015/17/input.txt").readLines().map { it.toInt() }
  val combinations = getCombinations(containers, totalEggnog).filter { it.sum() == totalEggnog }
  println(combinations.size)
  val minJars = combinations.minOf { it.size }
  println(combinations.count { it.size == minJars })
}

private fun getCombinations(containers: List<Int>, limit: Int): List<List<Int>> {
  var combos = listOf(emptyList(), listOf(containers.first()))
  for (i in 1 until containers.size) {
    // Small optimization: drop clusters of jars with more volume than we want.
    combos = combos + combos.map { it + containers[i] }.filter { it.sum() <= limit }
  }
  return combos
}
