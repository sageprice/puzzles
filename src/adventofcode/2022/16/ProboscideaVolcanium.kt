package adventofcode.`2022`.`16`

import java.io.File
import kotlin.math.min

fun main() {
  val match = Regex("Valve ([A-Z]{2}) has flow rate=(\\d+); tunnels? leads? to valves? ([A-Z, ]*)")
  val input = File("src/adventofcode/2022/16/input.txt").readLines()

  val valveConnections = mutableMapOf<String, List<String>>()
  val flows = mutableMapOf<String, Long>()
  input.forEach { l ->
    val (currValve, flow, others) = match.find(l)?.destructured ?: error("Failed to parse: \"$l\"")
    valveConnections[currValve] = others.split(", ")
    if (flow.toInt() != 0) flows[currValve] = flow.toLong()
  }
  val pathCosts = extractTransitionCosts(valveConnections, flows)

  // This is the slow part, presumably due to all the set/list duplication.
  val sequencesFromAA = findSequences("AA", pathCosts, 30, mutableSetOf())

  // Part 1
  println(sequencesFromAA.maxOfOrNull { getPathFlow(it, pathCosts, flows, 30) ?: 0 })

  // Part 2
  val alternatePaths =
    findSequences("AA", pathCosts, 26, mutableSetOf())
      .mapNotNull { p ->
        val cost = getPathFlow(p, pathCosts, flows, 26)
        if (cost == null) null else Pair(p.toSet(), cost) }
      .sortedByDescending { (_, b) -> b } // Start high so we can prune quickly
  var p2best = 0L
  alternatePaths.forEachIndexed { i, (p1, f1) ->
    for (j in i+1 until alternatePaths.size) {
      val (p2, f2) = alternatePaths[j]
      if (f1 + f2 < p2best) break
      if (f1 + f2 > p2best && p1.intersect(p2).size == 1) {
        p2best = f1 + f2
      }
    }
  }
  println(p2best)
}

private fun findSequences(
  start: String,
  paths: Map<String, MutableMap<String, Int>>,
  t: Int,
  visited: MutableSet<String>
): List<List<String>> {
  // Path is impossible, return
  val pathsFromHere = mutableListOf<List<String>>(listOf())
  if (t <= 0) return pathsFromHere

  visited.add(start)
  for ((room, cost) in paths[start]!!) {
    if (room !in visited) pathsFromHere.addAll(findSequences(room, paths, t - cost, visited))
  }
  visited.remove(start)
  return pathsFromHere.map { listOf(start) + it }
}

private fun getPathFlow(
  stops: List<String>,
  paths: Map<String, Map<String, Int>>,
  flows: Map<String, Long>,
  t: Int
): Long? {
  var s = t
  var total = 0L
  for (i in 1 until stops.size) {
    val moveCost = paths[stops[i-1]]!![stops[i]]!!
    s -= moveCost
    if (s < 0) return null
    if (s <= 1) break
    total += (s-1) * flows[stops[i]]!!
    s--
  }
  return total
}

private fun extractTransitionCosts(
  valveConnections: MutableMap<String, List<String>>,
  flows: MutableMap<String, Long>
): Map<String, MutableMap<String, Int>> {
  val pathCosts = mutableMapOf<String, MutableMap<String, Int>>()
  for (start in valveConnections.keys) for (end in valveConnections.keys) {
    if (start != end && (start == "AA" || flows.containsKey(start)) && flows.containsKey(end)) {
      val path = findShortestPath(start, end, valveConnections, pathCosts, mutableSetOf())
      if (pathCosts.containsKey(start)) {
        pathCosts[start]!![end] = path!!
      } else {
        pathCosts[start] = mutableMapOf(Pair(end, path!!))
      }
    }
  }
  return pathCosts
}

private fun findShortestPath(
  start: String,
  end: String,
  connections: Map<String, List<String>>,
  knownCosts: MutableMap<String, MutableMap<String, Int>>,
  visited: MutableSet<String>): Int? {
  if (start == end) error("It's the same picture: $start, $end")
  if (connections[start]?.contains(end) == true) return 1
  if (knownCosts[start]?.containsKey(end) == true) {
    return knownCosts[start]!![end]!!
  }
  var shortest: Int? = null
  for (room in connections[start] ?: emptyList()) {
    if (room in visited) continue
    visited.add(room)
    val pathLen = findShortestPath(room, end, connections, knownCosts, visited)
    if (pathLen != null) {
      shortest = min(1 + pathLen, shortest ?: 1000)
    }
    visited.remove(room)
  }
  if (shortest == null) return null
  if (knownCosts[start] != null) {
    knownCosts[start]!![end] = shortest
  } else {
    knownCosts[start] = mutableMapOf(Pair(end, shortest))
  }
  return shortest
}
