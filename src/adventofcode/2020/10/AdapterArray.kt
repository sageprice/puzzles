package adventofcode.`2020`.`10`

import java.io.File

fun main() {
  val adapters =
      File("src/adventofcode/2020/10/input.txt")
          .readLines()
          .map { it.toInt() }
          .sorted()

  // Part 1
  val diffs = sumDifferences(adapters)
  // Add a 3 b/c our device is the final adapter's joltage + 3
  println("Part 1: " + (diffs[1]!! * (diffs[3]!! + 1)))

  // Part 2
  println("Part 2: " + countSequences(adapters))
}

private fun sumDifferences(adapters: List<Int>): Map<Int, Int> {
  val diffCounts = mutableMapOf(Pair(1, 0), Pair(2, 0), Pair(3, 0))
  diffCounts[adapters.first() - 0] = diffCounts[adapters.first() - 0]!! + 1
  for (i in 1 until adapters.size) {
    diffCounts[adapters[i] - adapters[i-1]] = diffCounts[adapters[i] - adapters[i-1]]!! + 1
  }
  return diffCounts
}

private fun countSequences(adapters: List<Int>): Long {
  val sequences = mutableListOf<Long>()
  for (i in adapters.indices) {
    sequences.add(if(adapters[i] <= 3) 1 else 0)
    for (j in 1..3) {
      if (i-j < 0 || adapters[i] - adapters[i-j] > 3) break
      sequences[i] = sequences[i] + sequences[i-j]
    }
  }
  return sequences.last()
}

