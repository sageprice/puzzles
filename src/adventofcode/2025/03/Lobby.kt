package adventofcode.`2025`.`03`

import java.io.File

/** https://adventofcode.com/2025/day/3 */
fun main() {
  val banks = File("src/adventofcode/2025/03/input.txt")
    .readLines()
    .map {
      it.split("").filter { s -> s.isNotEmpty() }.map { s -> s.toInt() }
    }

  println(banks.sumOf { getMaxKBitJoltage(it, 2) })
  println(banks.sumOf {getMaxKBitJoltage(it, 12) })
}

private fun getMaxKBitJoltage(batteryBank: List<Int>, k: Int): Long {
  // Main idea: we always want to maximize each battery by taking the largest, leftmost battery
  // between the position of the preceding battery and the current battery. So scan from current
  // position to the left, update if needed, then repeat for each successive battery.
  val indices = MutableList(k) { i -> batteryBank.size - k + i}
  for (i in 0 until indices.size) {
    val lowEnd = if (i == 0) 0 else indices[i-1] + 1
    for (j in indices[i] - 1 downTo lowEnd) {
      if (batteryBank[j] >= batteryBank[indices[i]]) {
        indices[i] = j
      }
    }
  }
  return indices.map { batteryBank[it] }.joinToString("").toLong()
}
