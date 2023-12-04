package adventofcode.`2023`.`04`

import java.io.File
import kotlin.math.pow

fun main() {
  val scratchCards =
    File("src/adventofcode/2023/04/input.txt")
      .readLines()
      .map { parseScratchCard(it) }

  println(scratchCards.sumOf { getCardPoints(it) })
  println(getScratchCards(scratchCards).sum())
}

private fun getScratchCards(scs: List<ScratchCard>): List<Int> {
  val finalScs = Array(scs.size) { 1 }
  scs.indices.forEach { i ->
    (1..scs[i].getMatches()).forEach { j ->
      finalScs[i+j] += finalScs[i]
    }
  }
  return finalScs.toList()
}

private fun getCardPoints(sc: ScratchCard): Long {
  val matches = sc.getMatches()
  return if (matches == 0) 0
  else 2.0.pow(matches - 1).toLong()
}

private fun ScratchCard.getMatches(): Int {
  val mySet = this.myNums.toSet()
  return this.cardNums.count { it in mySet }
}

private fun parseScratchCard(s: String): ScratchCard {
  val (preamble, nums) = s.split(": ")
  val gameId = preamble.substring(4).trim().toInt()
  val (cardNums, myNums) = nums.trim().split(" | ")
  return ScratchCard(
    gameId,
    cardNums.split(" ").filter { it.isNotEmpty() }.map { it.toInt() },
    myNums.split(" ").filter { it.isNotEmpty() }.map { it.toInt() })
}

private data class ScratchCard(val id: Int, val cardNums: List<Int>, val myNums: List<Int>)