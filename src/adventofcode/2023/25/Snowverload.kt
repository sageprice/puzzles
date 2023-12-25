package adventofcode.`2023`.`25`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val (edges, graph) = readGraph()
  val longestShortest =
      edges
          .map { it.first }
          .distinct()
          .map { findShortestPathsFrom(it, graph).maxBy { p -> p.value.size } }
          .maxByOrNull { p -> p.value.size }!!
  val (p1, p2) = cutItDown(longestShortest.value, graph) ?: error("Wait where's the answer?")
  println(p1.size * p2.size)
}

private fun cutItDown(
    initialPath: List<String>, graph: Map<String, Set<String>>, cutsMade: Int = 0): List<Set<String>>? {
  if (cutsMade == 3) return null
  for (i in 0 until initialPath.size - 2) {
    val postCut = cutGraph(listOf(Pair(initialPath[i], initialPath[i+1])), graph)
    val newPath = findShortestPathsFrom(initialPath.first(), postCut)[initialPath.last()]
        ?: return getGraphPartitions(postCut)
    val result = cutItDown(newPath, postCut, cutsMade+1)
    if (result != null) return result
  }
  return null
}

private fun findShortestPathsFrom(start: String, graph: Map<String, Set<String>>): Map<String, List<String>> {
  val paths = mutableMapOf<String, List<String>>()
  val queue = PriorityQueue<Pair<String, List<String>>> { a, b -> a.second.size - b.second.size }
  queue.add(Pair(start, listOf(start)))
  val seen = mutableSetOf<String>()
  while (queue.isNotEmpty()) {
    val (point, path) = queue.poll()
    if (point in seen) continue
    seen.add(point)
    paths[point] = path
    graph[point]?.forEach { neighbor -> queue.add(Pair(neighbor, path + neighbor)) }
  }
  return paths
}

private fun cutGraph(
    edgesToCut: List<Pair<String, String>>, graph: Map<String, Set<String>>): Map<String, Set<String>> {
  val cut: MutableMap<String, MutableSet<String>> = graph.mapValues { (_, v) -> v.toMutableSet() }.toMutableMap()
  for ((a, b) in edgesToCut) {
    cut[a]?.remove(b)
    cut[b]?.remove(a)
  }
  return cut
}

private fun getGraphPartitions(graph: Map<String, Set<String>>): List<Set<String>> {
  val partitions = mutableListOf<Set<String>>()
  val seen = mutableSetOf<String>()
  for (k in graph.keys) {
    if (k in seen) continue
    val partition = getPartition(k, graph, seen)
    seen.addAll(partition)
    partitions.add(partition)
  }
  return partitions
}

private fun getPartition(start: String, graph: Map<String, Set<String>>, seen: Set<String>): Set<String> {
  val partition = mutableSetOf<String>()
  val queue = mutableSetOf(start)
  while (queue.isNotEmpty()) {
    val next = queue.first()
    queue.remove(next)
    if (next in seen) continue
    partition.add(next)
    graph[next]?.forEach { neighbor ->
      if (neighbor !in seen && neighbor !in queue && neighbor !in partition) queue.add(neighbor)
    }
  }
  return partition
}

private fun readGraph(): Pair<List<Pair<String, String>>, Map<String, Set<String>>> {
  val graph = mutableMapOf<String, MutableSet<String>>()
  val edges = mutableListOf<Pair<String, String>>()
  File("src/adventofcode/2023/25/input.txt").readLines().forEach { l ->
    val (left, right) = l.split(": ")
    for (part in right.split(" ")) {
      edges.add(Pair(left, part))
      if (left in graph) graph[left]?.add(part) else graph[left] = right.split(" ").toMutableSet()
      if (part in graph) graph[part]?.add(left) else graph[part] = mutableSetOf(left)
    }
  }
  return Pair(edges, graph)
}
