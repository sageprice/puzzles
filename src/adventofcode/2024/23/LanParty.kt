package adventofcode.`2024`.`23`

import java.io.File

/** https://adventofcode.com/2024/day/23 */
fun main() {
  val lanPairs = File("src/adventofcode/2024/23/input.txt").readLines().map { it.split("-") }
  val neighbors = constructNeighbors(lanPairs)
  val cycles = find3Cycles(neighbors)
  println(cycles.count { s -> s.any { it.startsWith("t") } })

  println(findLargestClique(neighbors).toList().sorted().joinToString(","))
}

private fun findLargestClique(neighbors: Map<String, Set<String>>): Set<String> =
  neighbors.keys.map { findLargestClique(setOf(it), neighbors) }.maxBy { it.size }

private val cache = mutableMapOf<Set<String>, Set<String>>()

private fun findLargestClique(seen: Set<String>, neighbors: Map<String, Set<String>>): Set<String> {
  if (seen in cache) return cache[seen]!!
  val candidates = seen.map { neighbors[it] ?: emptySet() }.reduce { a, b -> a.intersect(b) }.filter { it !in seen }
  if (candidates.isEmpty()) {
    cache[seen] = seen
    return seen
  }
  val best = candidates.map { findLargestClique(seen + it, neighbors) }.maxBy { it.size }
  cache[seen] = best
  return best
}

private fun find3Cycles(neighbors: Map<String, Set<String>>): Set<Set<String>> {
  val cycles = mutableSetOf<Set<String>>()
  for ((n0, ns) in neighbors) {
    for (n1 in ns) {
      for (n2 in (neighbors[n1] ?: emptySet())) {
        if (n0 in (neighbors[n2] ?: emptySet())) {
          cycles.add(setOf(n0, n1, n2))
        }
      }
    }
  }
  return cycles
}

private fun constructNeighbors(lanPairs: List<List<String>>): Map<String, Set<String>> {
  val neighborMaps = mutableMapOf<String, MutableSet<String>>()
  for ((a, b) in lanPairs) {
    if (a !in neighborMaps) neighborMaps[a] = mutableSetOf(b) else neighborMaps[a]?.add(b)
    if (b !in neighborMaps) neighborMaps[b] = mutableSetOf(a) else neighborMaps[b]?.add(a)
  }
  return neighborMaps
}
