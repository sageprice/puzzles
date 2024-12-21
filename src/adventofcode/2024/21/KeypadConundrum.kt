package adventofcode.`2024`.`21`

import java.io.File

private val cache = mutableMapOf<Pair<Char, Char>, List<String>>()

/** https://adventofcode.com/2024/day/21 */
fun main() {
  val input = File("src/adventofcode/2024/21/input.txt").readLines()

  getTransitions(digitKeypad)
  getTransitions(arrowKeypad)
  for (k in cache.keys) {
    cache[k] = cache[k]!!.map { it + "A" }
    val seqs = cache[k]!!
    val expandedLengths = seqs.associateWith { s ->
      getPossibleSequences(s.indices.map { i -> (s.getOrNull(i - 1) ?: 'A') to s[i] }).first()
    }
    val lengthOfShortest = expandedLengths.values.minBy { it.length }.length
    cache[k] = expandedLengths.filter { it.value.length == lengthOfShortest }.keys.toList()
  }
  var score = 0L
  for (code in input) {
    val seq = getFullSequence(code, 2)
    score += getComplexityScore(code, seq)
  }
  println(score)

//  score = 0L
//  for (code in input) {
//    val seq = getFullSequence(code, 25)
//    score += getComplexityScore(code, seq)
//  }
//  println(score)
}

private fun getComplexityScore(code: String, seq: String) =
  code.substring(0, code.length - 1).toLong() * seq.length

private fun getFullSequence(code: String, robotRounds: Int): String {
  val pairs = code.indices.map { i -> (code.getOrNull(i - 1) ?: 'A') to code[i] }
  var moves = getPossibleSequences(pairs)
  repeat(robotRounds) {
    moves = getShortestMoves(moves)
  }
  return moves.first()
}

private fun getShortestMoves(priorSeqs: List<String>): List<String> {
  val pairs: List<List<Pair<Char, Char>>> =
    priorSeqs.map { seq -> seq.indices.map { i -> (seq.getOrNull(i - 1) ?: 'A') to seq[i] } }
  val newMoves = pairs.flatMap { getPossibleSequences(it) }
  val shortest = newMoves.minBy { it.length }
  return newMoves.filter { it.length == shortest.length }
}

private fun getPossibleSequences(pairs: List<Pair<Char, Char>>): List<String> {
  var possibilities = listOf<String>()
  for (xs in pairs.map { p -> cache[p]!! }) {
    possibilities = if (possibilities.isEmpty()) xs else possibilities.flatMap { p -> xs.map { x -> p + x } }
  }
  return possibilities
}

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
