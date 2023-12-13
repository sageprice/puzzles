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

  var count = 0L
  for (i in springRecords.indices) {
    val (spr, seq) = springRecords[i].expand(5)
    count += getValidConfigurations(spr, seq)
    println("Finished with ${i+1} records, last was [$spr, $seq]")
  }
  println(count)
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
//  println(" ".repeat(index) + "Getting configurations for $springs")
  if (index == springs.length) {
//    println("Success! $springs, $sequence")
    return 1L
  }
  if (springs[index] != '?') return getValidConfigurations(springs, sequence, index + 1)
  var results = 0L
  val busted = springs.replaceRange(index.. index, ".")
//  println(" ".repeat(index+2) + busted)
  if (isOkaySequence(busted, sequence)) {
    results += getValidConfigurations(busted, sequence, index+1)
  }
  val working = springs.replaceRange(index.. index, "#")
//  println(" ".repeat(index+2) + working)
  if (isOkaySequence(working, sequence)) {
    results += getValidConfigurations(working, sequence, index+1)
  }
  return results
}

private fun isOkaySequence(springs: String, sequence: List<Int>): Boolean {
  if (!springs.contains("?")) {
    val cleaned = springs.replace(".", " ").trim().split(" ").filter { it.isNotBlank() }
    return cleaned.size == sequence.size && cleaned.zip(sequence).all { (a, b) -> a.length == b }
  }
  val idx = springs.indexOfFirst { it == '?' }
  val fixed = springs.substring(0 until idx)
  val tail = springs.substring(idx)
  if (fixed.isEmpty()) return true
  val cleaned = fixed.replace(".", " ").trim().split(" ").filter { it.isNotBlank() }
//  println("$fixed -> $cleaned")
  if (cleaned.size > sequence.size) return false
  val tailSeq = sequence.subList(cleaned.size, sequence.size)
  if (isOkayTail(tail, tailSeq))
  for (i in 0 until cleaned.size - 1) {
    if (cleaned[i].length != sequence[i]) {
      return false
    }
  }
//  println("$cleaned :==: $sequence")
  if (cleaned.isEmpty()) return true
  return cleaned.last().length == sequence[cleaned.size-1] ||
      (fixed.endsWith("#") && cleaned.last().length < sequence[cleaned.size-1])
}

private fun isOkayTail(tail: String, sequence: List<Int>): Boolean {
  if (tail.length < sequence.sum() + sequence.size - 1) return false
  if (tail.count { it == '#' } > sequence.sum()) return false
  return true
}

private data class SpringRecord(val springs: String, val sequence: List<Int>)