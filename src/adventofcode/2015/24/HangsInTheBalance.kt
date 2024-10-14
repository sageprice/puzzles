package adventofcode.`2015`.`24`

import java.io.File
import kotlin.math.min

fun main() {
  val weights = File("src/adventofcode/2015/24/input.txt").readLines().map { it.toLong() }
  println(getDistributions(weights, weights.sum() / 3))
  println(getDistributions(weights, weights.sum() / 4))
}

private fun getDistributions(weights: List<Long>, target: Long): Long {
  val dists = getDistributionsRecursively(weights, target, emptyList())
  val grouped = dists.groupBy { it.size }
  val shortest = grouped[grouped.keys.min()]
  return shortest!!.minOfOrNull { it.reduce { a, b -> a * b } }!!
}

private val seen = mutableMapOf<Triple<List<Long>, Long, List<Long>>, Set<List<Long>>>()
private var shortestFound = Int.MAX_VALUE

private fun getDistributionsRecursively(weights: List<Long>, target: Long, used: List<Long>): Set<List<Long>> {
  if (used.size > shortestFound) return emptySet()
  if (target < 0) return emptySet()
  val t = Triple(weights, target, used)
  val cached = seen[t]
  if (cached != null) {
    return cached
  }
  if (used.size > 8) return emptySet()
  val distributions = mutableSetOf<List<Long>>()
  for (i in weights.indices) {
    if (weights[i] > target) continue
    else if (weights[i] == target) {
      distributions.add(used + weights[i])
      shortestFound = min(shortestFound, used.size + 1)
      continue
    }
    val remainingWeights = weights.subList(i+1, weights.size)
    distributions.addAll(getDistributionsRecursively(remainingWeights, target, used))
    distributions.addAll(getDistributionsRecursively(remainingWeights, target - weights[i], used + weights[i]))
  }
  seen[t] = distributions
  return distributions
}
