package adventofcode.`2025`.`05`

import java.io.File

/** https://adventofcode.com/2025/day/5 */
fun main() {
  val input = File("src/adventofcode/2025/05/input.txt").readText()
  val (rawRanges, rawIngredients) = input.split("\n\n")
  val ranges = rawRanges.split("\n").map {
    val (a, b) = it.split("-")
    a.toLong()..b.toLong()
  }
  val ingredients = rawIngredients.split("\n").map { it.toLong() }

  println(getFreshIngredients(ranges, ingredients).size)
  println(getCombinedRanges(ranges).sumOf { it.last - it.first + 1 })
}

private fun getFreshIngredients(ranges: List<LongRange>, ingredients: List<Long>): List<Long> {
  return ingredients.filter { i ->
    ranges.any { i in it }
  }
}

private fun getCombinedRanges(ranges: List<LongRange>): List<LongRange> {
  val combinedRanges = mutableListOf<LongRange>()
  val sortedRanges = ranges.sortedBy { it.first }
  // For simplicity, remove the ranges which are fully contained in the preceding one.
  val filteredRanges = sortedRanges.filterIndexed { idx, range ->
    !(idx > 0 && range.last <= sortedRanges[idx-1].last)
  }
  for (range in filteredRanges) {
    if (combinedRanges.isEmpty()) {
      combinedRanges.add(range)
      continue
    }
    if (range.first > combinedRanges.last().last) {
      combinedRanges.add(range)
    } else {
      val r = combinedRanges.removeLast()
      val s = r.first..range.last
      combinedRanges.add(s)
    }
  }
  return combinedRanges
}
