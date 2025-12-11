package adventofcode.`2025`.`11`

import java.io.File

/** https://adventofcode.com/2025/day/11 */
fun main() {
  val input = File("src/adventofcode/2025/11/input.txt").readLines()

  val graph = buildGraph(input)
  println(getPathsBetween(graph, "you", "out"))
  println(getPathsThroughDacAndFft(graph, "svr", "out"))
}

private val pathCounts = mutableMapOf<Pair<String, String>, Long>()

private fun getPathsBetween(graph: Map<String, List<String>>, start: String, end: String): Long {
  val seenPaths = pathCounts[start to end]
  if (seenPaths != null) return seenPaths

  if (start == end) return 1

  var count = 0L
  val nextPoints = graph[start]
  nextPoints?.forEach { point ->
    count += getPathsBetween(graph, point, end)
  }
  pathCounts[start to end] = count
  return count
}

private fun getPathsThroughDacAndFft(graph: Map<String, List<String>>, start: String, end: String): Long {
  val dacToFft = getPathsBetween(graph, "dac", "fft")
  val order = if (dacToFft > 0) {
    listOf(start, "dac", "fft", end)
  } else {
    listOf(start, "fft", "dac", end)
  }
  return (1 until order.size).map { i ->
    getPathsBetween(graph, order[i-1], order[i])
  }.reduce { a, b -> a*b }
}

private fun buildGraph(connections: List<String>): Map<String, List<String>> {
  val graph = mutableMapOf<String, List<String>>()
  connections.forEach { l ->
    val (start, outs) = l.split(": ")
    graph[start] = outs.split(" ")
  }
  return graph
}
