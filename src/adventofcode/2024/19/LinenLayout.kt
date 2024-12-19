package adventofcode.`2024`.`19`

import java.io.File
import kotlin.math.min

/** https://adventofcode.com/2024/day/19 */
fun main() {
  val input = File("src/adventofcode/2024/19/input.txt").readText()
  val (one, two) = input.split("\n\n")
  val available = one.split(", ").toSet()
  val designs = two.split("\n")

  val waysToMakeDesigns = getWaysToMakeDesigns(designs, available)
  println(waysToMakeDesigns.count { it > 0 })
  println(waysToMakeDesigns.sumOf { it })
}

private fun getWaysToMakeDesigns(designs: List<String>, available: Set<String>): List<Long> {
  val longest = available.maxOf { it.length }
  return designs.map { getWaysCount(it, available, longest) }
}

private val cache = mutableMapOf<String, Long>()

private fun getWaysCount(design: String, available: Set<String>, maxLen: Int): Long {
  if (design in cache) return cache[design]!!
  if (design.isEmpty()) return 1
  val count = (1..min(maxLen, design.length)).sumOf { i ->
    if (design.substring(0, i) !in available) 0
    else getWaysCount(design.substring(i), available, maxLen)
  }
  cache[design] = count
  return count
}
