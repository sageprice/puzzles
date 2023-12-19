package adventofcode.`2023`.`19`

import java.io.File

fun main() {
  val (first, second) =
    File("src/adventofcode/2023/19/input.txt").readText().split("\r\n\r\n")
  val workflows = first.split("\r\n").associate { parseWorkflow(it) }
  val parts = second.split("\r\n").map { parsePart(it) }

  val accepted = parts.filter { isPartAccepted(it, workflows) }
  println(accepted.sumOf { totalPartValues(it) })

  val range = (1..4000).toSet()
  val possibilities = mapOf(
    Pair("x", range),
    Pair("m", range),
    Pair("a", range),
    Pair("s", range),
  )
  println(countValidRanges("in", possibilities, workflows))
}

val visited = mutableSetOf<String>()

// TODO: it would be more efficient to just track upper and lower end of each range, rather than keep a set.
private fun countValidRanges(
  curr: String, ranges: Map<String, Set<Int>>, workflows: Map<String, List<WorkflowAction>>): Long {
  if (curr == "A") return ranges.values.map { it.size.toLong() }.reduce { a, b -> a * b }
  if (curr == "R") return 0
  // Safety check. If this doesn't work we'll have to de-dupe ranges.
  if (curr !in visited) visited.add(curr) else error("We're in a DAG! We've hit $curr twice")
  val newRanges = ranges.toMutableMap()
  var count = 0L
  for (action in workflows[curr]!!) {
    if (action is Dest) count += countValidRanges(action.x, newRanges, workflows)
    if (action is Comp) {
      val (component, comparator, threshold, dest) = action
      val currCompVals = newRanges[component]!!
      val passingCompVals = currCompVals.filter { comparePart(it, comparator, threshold) }.toSet()
      val failingCompVals = currCompVals.filter { it !in passingCompVals }.toSet()
      newRanges[component] = passingCompVals
      count += countValidRanges(dest.x, newRanges, workflows)
      newRanges[component] = failingCompVals
    }
  }
  return count
}

private fun totalPartValues(part: Part): Long = part.x.toLong() + part.m + part.a + part.s

private fun isPartAccepted(part: Part, workflows: Map<String, List<WorkflowAction>>): Boolean {
  var current = Dest("in")
  while (current.x != "A" && current.x != "R") {
    val workflow = workflows[current.x] ?: error("Could not find workflow keyed by $current")
    for (action in workflow) {
      if (action is Dest) {
        current = action
        break
      }
      if (action is Comp && passesComp(part, action)) {
        current = action.dest
        break
      }
    }
  }
  return current.x == "A"
}

private fun passesComp(part: Part, comp: Comp): Boolean {
  return when (comp.component) {
    "x" -> comparePart(part.x, comp.comparator, comp.threshold)
    "m" -> comparePart(part.m, comp.comparator, comp.threshold)
    "a" -> comparePart(part.a, comp.comparator, comp.threshold)
    "s" -> comparePart(part.s, comp.comparator, comp.threshold)
    else -> error("Unknown part type requested from $part: $comp")
  }
}

private fun comparePart(x: Int, comparator: String, threshold: Int): Boolean {
  return when (comparator) {
    ">" -> x > threshold
    "<" -> x < threshold
    else -> error("Unknown operator: $comparator, comparing $x against $threshold")
  }
}

private fun parsePart(input: String): Part {
  val (x, m, a, s) = input.substring(1, input.length - 1).split(",")
  return Part(
    x.substring(2).toInt(),
    m.substring(2).toInt(),
    a.substring(2).toInt(),
    s.substring(2).toInt()
  )
}

private data class Part(val x: Int, val m: Int, val a: Int, val s: Int)

private fun parseWorkflow(input: String): Pair<String, List<WorkflowAction>> {
  val (name, rest) = input.split("{")
  val actions = rest.substring(0, rest.length-1).split(",").map { parseAction(it) }
  return Pair(name, actions)
}

private fun parseAction(action: String): WorkflowAction {
  return if (action.contains(":")) {
    val (comp, dest) = action.split(":")
    Comp(comp.first().toString(), comp[1].toString(), comp.substring(2).toInt(), Dest(dest))
  } else Dest(action)
}

private sealed class WorkflowAction
private data class Dest(val x: String) : WorkflowAction()
private data class Comp(
  val component: String, val comparator: String, val threshold: Int, val dest: Dest): WorkflowAction()
