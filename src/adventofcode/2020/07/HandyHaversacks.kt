package adventofcode.`2020`.`07`

import java.io.File

fun main() {
  val graph = buildBagGraph(
      File("src/adventofcode/2020/07/input.txt")
          .readLines()
          .map { extractBagRule(it) })

  // Part 1
  println("Part 1: " + findAncestors("shiny gold", graph).size)

  // Part 2
  println("Part 2: " + findDescendantsCount("shiny gold", graph))
}

private fun findDescendantsCount(start:String, graph: Map<String, Node>): Int {
  val toVisit = graph[start]!!.children.toMutableList()
  var bagCount = 0
  while (toVisit.isNotEmpty()) {
    val (bag, count) = toVisit.removeAt(0)
    bagCount += count
    graph[bag]?.children?.forEach {
      toVisit.add(Pair(it.first, it.second * count))
    }
  }
  return bagCount
}

private fun findAncestors(start: String, graph: Map<String, Node>): List<String> {
  val toVisit = graph[start]!!.parents.toMutableList()
  val visited = mutableSetOf(start)
  while (toVisit.isNotEmpty()) {
    val next: String = toVisit.removeAt(0)
    visited.add(next)
    graph[next]?.parents?.forEach {
      if (!visited.contains(it)) {
        toVisit.add(it)
      }
    }
  }
  return (visited - setOf(start)).toList()
}

private data class Node(
    val bag: String,
    val parents: MutableSet<String> = mutableSetOf(),
    val children: MutableSet<Pair<String, Int>> = mutableSetOf())

private fun buildBagGraph(rules: List<BagRule>): Map<String, Node> {
  val graph = mutableMapOf<String, Node>()
  rules.forEach { rule ->
    if (graph.containsKey(rule.bag)) {
      val node = graph[rule.bag]!!
      for (c in rule.contained) {
        node.children.add(c)
      }
    } else {
      graph[rule.bag] = Node(rule.bag, children = rule.contained.toMutableSet())
    }
    for (child in rule.contained) {
      if (graph.containsKey(child.first)) {
        val cNode = graph[child.first]
        cNode!!.parents.add(rule.bag)
      } else {
        graph[child.first] = Node(child.first, parents = mutableSetOf(rule.bag))
      }
    }
  }
  return graph
}

private data class BagRule(val bag: String, val contained: List<Pair<String, Int>>)

private fun extractBagRule(rule: String): BagRule {
  val outerBag = Regex("^(\\w+ \\w+)").find(rule)?.value!!
  val contained = Regex("(\\d) (\\w+ \\w+) bag")
      .findAll(rule)
      .asIterable()
      .map {
        val result = it.destructured.toList()
        Pair(result[1], result[0].toInt())
      }.toList()
  return BagRule(outerBag, contained)
}