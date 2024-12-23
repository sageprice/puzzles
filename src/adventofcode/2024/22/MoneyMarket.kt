package adventofcode.`2024`.`22`

import java.io.File

/** https://adventofcode.com/2024/day/22 */
fun main() {
  val initialSecrets = File("src/adventofcode/2024/22/input.txt").readLines().map { it.toLong() }

  // Part 1
  val allSecrets = initialSecrets.map { getNSecrets(it, 2000) }
  println(allSecrets.sumOf { it.last() })

  // Part 2
  println(getMaxProfit(allSecrets))
}

private fun getMaxProfit(allSecrets: List<List<Long>>): Long? {
  val allSellValues = allSecrets.map { getSellValues(it) }
  return allSellValues.map { it.keys }.flatten().distinct()
    .maxOfOrNull { sp -> allSellValues.sumOf { m -> m[sp] ?: 0 } }
}

private fun getSellValues(secrets: List<Long>): Map<List<Long>, Long> {
  val onesPlaces = secrets.map { it % 10 }
  val sellPrices: List<Pair<List<Long>, Long>> = (4 until onesPlaces.size).map { i ->
    (-3..0).map { j -> onesPlaces[i+j] - onesPlaces[i+j-1] } to onesPlaces[i]
  }
  val sellValues = mutableMapOf<List<Long>, Long>()
  for ((indicator, price) in sellPrices) {
    // We only want the first time we see the sequence.
    if (indicator !in sellValues) sellValues[indicator] = price
  }
  return sellValues
}

private fun getNSecrets(s: Long, n: Int): List<Long> {
  val secrets = mutableListOf(s)
  repeat(n) {
    secrets.add(getNext(secrets.last()))
  }
  return secrets
}

private fun getNext(secret: Long): Long {
  var s = ((secret * 64L).xor(secret)) % 16777216L
  s = (s / 32).xor(s) % 16777216L
  s = (s * 2048).xor(s) % 16777216L
  return s
}
