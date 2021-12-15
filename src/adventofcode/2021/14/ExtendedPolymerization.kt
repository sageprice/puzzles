package adventofcode.`2021`.`14`

import java.io.File

fun main() {
  val (startSequence, lines) =
    File("src/adventofcode/2021/14/input.txt").readText().split("\n\n")
  val transforms: Map<String, String> = lines
    .split("\n")
    .map { it.split(" -> ") }
    .map { (a, b) -> Pair(a, b) }.toMap()

  // Part 1
  val letters = startSequence
    .split("")
    .filter { it.isNotBlank() }
    .toList()
  val mutableSeqMap = mutableMapOf<Pair<String, String>, Long>()
  for (i in 1 until letters.size) {
    val p = Pair(letters[i - 1], letters[i])
    if (mutableSeqMap.containsKey(p)) {
      mutableSeqMap[p] = mutableSeqMap[p]!! + 1
    } else mutableSeqMap[p] = 1
  }
  var seqMap = mutableSeqMap.toMap()
  repeat(10) { seqMap = apply(seqMap, transforms) }
  var counts = countLetters(seqMap, letters.last())
  println(counts.maxOf { (_, v) -> v } - counts.minOf { (_, v) -> v} )

  // Part 2
  repeat (30) { seqMap = apply(seqMap, transforms) }
  counts = countLetters(seqMap, letters.last())
  println(counts.maxOf { (_, v) -> v } - counts.minOf { (_, v) -> v} )
}

private fun apply(
  seq: Map<Pair<String, String>, Long>,
  transforms: Map<String, String>
): Map<Pair<String, String>, Long> {
  val nextSeq = mutableMapOf<Pair<String, String>, Long>()
  for ((k, v) in seq) {
    val (a, b) = k
    // left side
    val left = Pair(a, transforms[a+b]!!)
    if (nextSeq.containsKey(left)) nextSeq[left] = v + nextSeq[left]!!
    else nextSeq[left] = v
    // right side
    val right = Pair(transforms[a+b]!!, b)
    if (nextSeq.containsKey(right)) nextSeq[right] = v + nextSeq[right]!!
    else nextSeq[right] = v
  }
  return nextSeq
}

private fun countLetters(seq: Map<Pair<String, String>, Long>, lastLetter: String): Map<String, Long> {
  val counts = mutableMapOf(Pair(lastLetter, 1L))
  seq.forEach { (k, v) ->
    // Only count the first letter, otherwise we'll double count everything but the first/last.
    val (c, _) = k
    if (counts[c] != null) counts[c] = counts[c]!! + v
    else counts[c] = v
  }
  return counts
}