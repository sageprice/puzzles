package adventofcode.`2023`.`12`

import java.io.File

fun main() {
  val springRecords =
    File("src/adventofcode/2023/12/input.txt")
      .readLines()
      .map { it.split(" ") }
      .map { (springs, seq) ->
        SpringRecord(springs, seq.split(",").map { it.toInt() })
      }

  println(springRecords.sumOf { (spr, seq) -> getValidConfigurations(spr, seq) })

  println(springRecords.map { it.expand(5) }.sumOf { (spr, seq) -> getValidConfigurations(spr, seq) })
}

private fun SpringRecord.expand(k: Int): SpringRecord {
  var newSprings = springs
  val seq = sequence.toMutableList()
  for (i in 2..k) {
    newSprings += "?$springs"
    seq.addAll(sequence)
  }
  return SpringRecord(newSprings, seq)
}

private val cache = mutableMapOf<Pair<String, List<Int>>, Long>()

private fun getValidConfigurations(springs: String, sequence: List<Int>, index: Int = 0): Long {
  // Oh look, we made it
  if (index == springs.length) {
    return 1L
  }
  val prefix = springs.substring(0, index)
  val suffix = springs.substring(index)
  val parts = prefix.replace(".", " ").trim().split(" ").filter { it.isNotBlank() }
  var remainingSeq: List<Int>? = null
  // We try to cache whenever there is a fully defined sequence of springs in the prefix (no potential continuation).
  if ((index > 0 && springs[index-1] == '.') && parts.zip(sequence).all { (p, l) -> p.length == l }) {
    remainingSeq = sequence.subList(parts.size, sequence.size)
    val suffixCount = cache[Pair(suffix, remainingSeq)]
    if (suffixCount != null) return suffixCount
  }
  var results = 0L
  // Just iterate onwards.
  if (springs[index] != '?') {
    results = getValidConfigurations(springs, sequence, index + 1)
    if (remainingSeq != null) cache[Pair(suffix, remainingSeq)] = results
    return results
  }
  // Handle replacements
  val busted = springs.replaceRange(index.. index, ".")
  if (isOkaySequence(busted, sequence)) {
    results += getValidConfigurations(busted, sequence, index+1)
  }
  val working = springs.replaceRange(index.. index, "#")
  if (isOkaySequence(working, sequence)) {
    results += getValidConfigurations(working, sequence, index+1)
  }
  if (remainingSeq != null) cache[Pair(suffix, remainingSeq)] = results
  return results
}

private fun isOkaySequence(springs: String, sequence: List<Int>): Boolean {
  if (!springs.contains("?")) {
    val cleaned = getPrefixSpringGroups(springs)
    return cleaned.size == sequence.size && cleaned.zip(sequence).all { (a, b) -> a.length == b }
  }
  val idx = springs.indexOfFirst { it == '?' }
  val prefix = springs.substring(0 until idx)
  if (prefix.isEmpty()) return true
  val cleaned = getPrefixSpringGroups(prefix)
  if (cleaned.size > sequence.size) return false
  for (i in 0 until cleaned.size - 1) {
    if (cleaned[i].length != sequence[i]) {
      return false
    }
  }
  if (cleaned.isEmpty()) return true
  return cleaned.last().length == sequence[cleaned.size-1] ||
      (prefix.endsWith("#") && cleaned.last().length < sequence[cleaned.size-1])
}

private fun getPrefixSpringGroups(springs: String): List<String> =
  springs.replace(".", " ").trim().split(" ").filter { it.isNotBlank() }

private data class SpringRecord(val springs: String, val sequence: List<Int>)
