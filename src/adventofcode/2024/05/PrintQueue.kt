package adventofcode.`2024`.`05`

import java.io.File

/** https://adventofcode.com/2024/day/5 */
fun main() {
  val input = File("src/adventofcode/2024/05/input.txt").readText()
  val (inputOrdering, inputUpdates) = input.split("\n\n")

  val ordering: Map<Int, Set<Int>> = inputOrdering.split("\n").map { l ->
    val (a, b) = l.split("|").map { it.toInt() }
    a to b
  }.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second }.toSet() }

  val updates: List<List<Int>> = inputUpdates.split("\n").map { l ->
    l.split(",").map { it.toInt() }
  }

  // Part 1
  val (correct, incorrect) = updates.partition { isValidUpdate(it, ordering) }
  println(sumMiddleElements(correct))
  // Part 2
  println(sumMiddleElements(incorrect.map { sortUpdate(it, ordering) }))
}

private fun sumMiddleElements(lls: List<List<Int>>): Int = lls.sumOf { l -> l[l.size / 2] }

private fun isValidUpdate(update: List<Int>, ordering: Map<Int, Set<Int>>): Boolean {
  for (j in update.size - 1 downTo 0) {
    val mustFollowJ = ordering[update[j]] ?: continue
    for (i in 0 until j) {
      if (update[i] in mustFollowJ) return false
    }
  }
  return true
}

private fun sortUpdate(update: List<Int>, ordering: Map<Int, Set<Int>>): List<Int> {
  return update.sortedWith(Comparator { l, r ->
    val lFollowers = ordering[l] ?: emptySet()
    val rFollowers = ordering[r] ?: emptySet()
    return@Comparator when {
      l in rFollowers -> 1
      r in lFollowers -> -1
      else -> 0
    }
  })
}
