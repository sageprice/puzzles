package adventofcode.`2024`.`21`

import java.io.File

private typealias Move = Pair<Char, Char>

private val cache = mutableMapOf<Move, List<String>>()

/** https://adventofcode.com/2024/day/21 */
fun main() {
  val input = File("src/adventofcode/2024/21/input.txt").readLines()

  getTransitions(digitKeypad)
  getTransitions(arrowKeypad)
  for (k in cache.keys) {
    cache[k] = cache[k]!!.map { it + "A" }
  }
  var score = 0L
  for (code in input) {
    score += code.substring(0, code.length - 1).toLong() * getShortestInputLength(code, 2)
  }
  println(score)

  score = 0L
  for (code in input) {
    score += code.substring(0, code.length - 1).toLong() * getShortestInputLength(code, 25)
  }
  println(score)
}

/// MARK: implementation

private fun getShortestInputLength(code: String, robotRounds: Int): Long {
  var moves = getShortestInputSequences(listOf(expandToMoves("A$code")))
  repeat(robotRounds) {
    moves = getShortestInputSequences(moves)
  }
  return moves.first().values.sum()
}

private fun getShortestInputSequences(priorMoves: List<Map<Move, Long>>): List<Map<Move, Long>> {
  val newMoves = mutableListOf<Map<Move, Long>>()
  for (priorMove in priorMoves) {
    val expanded = priorMove.map { (move, count) ->
      cache[move]!!.map {
        // Prefix with an A to handle transitions between expansions
        expandToMoves("A$it", count = count)
      }
    }
    newMoves.addAll(buildSequences(expanded))
  }
  // The answer will only ever come from the shortest results at each step. Small performance gain.
  val minLength = newMoves.minOfOrNull { ms -> ms.values.sum() }
  return newMoves.filter { ms -> ms.values.sum() == minLength }
}

/**
 * Expands the input [str] into consecutive characters, then groups and tallies those pairs.
 *
 * E.g. for the string >>^>>A, the pairs are [>>, >^, ^>, >>, >A], so the final output is:
 * {
 *   [> to >] to 2,
 *   [> to ^] to 1,
 *   [^ to >] to 1,
 *   [> to A] to 1
 * }
 */
private fun expandToMoves(str: String, count: Long = 1): Map<Move, Long> {
  return (1 until str.length)
        .map { str[it-1] to str[it] }
        .groupBy { it }
        .mapValues { it.value.size.toLong() * count }
}

private fun buildSequences(moveSets: List<List<Map<Move, Long>>>): List<Map<Move, Long>> {
  if (moveSets.size <= 1) return moveSets.first().map { it.toMap().toMutableMap() }
  val endSequences = buildSequences(moveSets.subList(1, moveSets.size))
  return moveSets.first().flatMap { f -> combine(endSequences, f) }
}

/** Adds the moves in [toAdd] to each of the maps in [processed]. */
private fun combine(processed: List<Map<Move, Long>>, toAdd: Map<Move, Long>): List<Map<Move, Long>> {
  return processed.map {
    val agg = it.toMutableMap()
    toAdd.forEach { (m, c) ->
      agg[m] = agg.getOrDefault(m, 0) + c
    }
    agg
  }
}

/// MARK: keyboard navigation cache construction
private fun getTransitions(keypad: List<List<Char?>>) {
  val keys = keypad.flatten().filterNotNull()
  for (key1 in keys) for (key2 in keys) {
    recursiveKeyboardWalk(key1, key2, keypad)
  }
}

private fun recursiveKeyboardWalk(k1: Char?, k2: Char?, keyboard: List<List<Char?>>): List<String>? {
  if (k1 == null || k2 == null) return null
  if (k1 to k2 in cache) return cache[k1 to k2]!!
  if (k1 == k2) {
    cache[k1 to k2] = mutableListOf("")
    return cache[k1 to k2]
  }
  val (r, c) = getIndices(k1, keyboard)
  val (r1, c1) = getIndices(k2, keyboard)
  val paths = mutableSetOf<String>()
  if (c < c1) recursiveKeyboardWalk(keyboard[r][c+1], k2, keyboard)?.let { p ->
    paths.addAll(p.map { ">$it" })
  }
  if (r < r1) recursiveKeyboardWalk(keyboard[r+1][c], k2, keyboard)?.let { p ->
    paths.addAll(p.map { "v$it" })
  }
  if (r > r1) recursiveKeyboardWalk(keyboard[r-1][c], k2, keyboard)?.let { p ->
    paths.addAll(p.map { "^$it" })
  }
  if (c > c1) recursiveKeyboardWalk(keyboard[r][c-1], k2, keyboard)?.let { p ->
    paths.addAll(p.map { "<$it" })
  }
  cache[k1 to k2] = paths.toList()
  return cache[k1 to k2]
}

private fun getIndices(key: Char, keypad: List<List<Char?>>): Pair<Int, Int> {
  for (r in keypad.indices) for (c in keypad[r].indices) {
    if (keypad[r][c] == key) return r to c
  }
  throw IllegalStateException("Could not find key [$key] in keypad")
}

private val digitKeypad = listOf(
  listOf('7', '8', '9'),
  listOf('4', '5', '6'),
  listOf('1', '2', '3'),
  listOf(null, '0', 'A')
)

private val arrowKeypad = listOf(
  listOf(null, '^', 'A'),
  listOf('<', 'v', '>')
)
