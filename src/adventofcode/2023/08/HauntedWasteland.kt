package adventofcode.`2023`.`08`

import java.io.File

fun main() {
  val (turns, pivots) =
    File("src/adventofcode/2023/08/input.txt")
      .readText()
      .split("\r\n\r\n")
  val nodes = pivots.split("\r\n").map { parseLine(it) }
  val wasteland = nodes.associate { Pair(it.start, Pair(it.left, it.right)) }

  // Part 1
  println(escapeMaze(turns, wasteland))

  // Part 2
  println(
    wasteland
      .keys
      .filter { it.endsWith('A') }
      .map { findEscapeSteps(it, turns, wasteland) }
      // Only safe since I manually verified each starting point only hits one end point, with consistent loop length.
      .map { it.first() }
      .reduce { a, b -> lcm(a, b) })
}

// GCD and LCM taken from Rosetta Code: https://rosettacode.org/wiki/Least_common_multiple#Kotlin
private fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
private fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b

private fun findEscapeSteps(
  start: String,
  turns: String,
  wasteland: Map<String, Pair<String, String>>
): List<Long> {
  // We collect all possible exit lengths, and keep the relative position in the instructions to de-dupe
  val escapes =
    wasteland
      .keys
      .filter { it.endsWith('Z') }
      .associateWith { mutableListOf<Pair<Int, Int>>() }
      .toMutableMap()
  var curr = start
  var index = 0
  while (true) {
    val action = turns[index++ % turns.length]
    curr = if (action == 'L') {
      wasteland[curr]?.first ?: error("No node $curr in map")
    } else {
      wasteland[curr]?.second ?: error("No node $curr in map")
    }
    if (curr.endsWith('Z')) {
      val priorEscapes = escapes[curr]!!
      if ((index % turns.length) !in priorEscapes.map { it.first }) {
        priorEscapes.add(Pair(index % turns.length, index))
      } else {
        return priorEscapes.map { it.second.toLong() }
      }
    }
  }
}

private fun escapeMaze(
  turns: String,
  wasteland: Map<String, Pair<String, String>>
): Int {
  var curr = "AAA"
  var index = 0
  while (curr != "ZZZ") {
    val action = turns[index++ % turns.length]
    curr = if (action == 'L') {
      wasteland[curr]?.first ?: error("No node $curr in map")
    } else {
      wasteland[curr]?.second ?: error("No node $curr in map")
    }
  }
  return index
}

private fun parseLine(s: String): Node {
  val (curr, options) = s.split(" = ")
  return Node(curr, options.substring(1, 4), options.substring(6, 9))
}

private data class Node(val start: String, val left: String, val right: String)