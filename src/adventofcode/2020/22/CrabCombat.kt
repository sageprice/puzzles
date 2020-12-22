package adventofcode.`2020`.`22`

import java.io.File
import kotlin.math.max

fun main() {
  val decks: List<List<Int>> =
      File("src/adventofcode/2020/22/input.txt")
          .readText()
          .split("\n\n")
          .map { it.split("\n") }
          .map { ls -> ls.subList(1, ls.size).map { it.toInt() }}

  val (d1, d2) = playCrabCombat(decks[0], decks[1])
  println("Part 1: ${max(scoreDeck(d1), scoreDeck(d2))}")

  val (_, rd1, rd2) = playRecursiveCombat(decks[0], decks[1])
  println("Part 2: ${max(scoreDeck(rd1), scoreDeck(rd2))}")
}

private fun scoreDeck(deck: Deck): Long =
    deck.mapIndexed { index, v -> (deck.size - index) * v.toLong() }.sum()

private fun playCrabCombat(myDeck: Deck, crabDeck: Deck): Pair<Deck, Deck> {
  val d1 = myDeck.toMutableList()
  val d2 = crabDeck.toMutableList()

  while (d1.isNotEmpty() && d2.isNotEmpty()) {
    val c1 = d1.removeAt(0)
    val c2 = d2.removeAt(0)
    if (c1 < c2) {
      d2.add(c2)
      d2.add(c1)
    } else {
      d1.add(c1)
      d1.add(c2)
    }
  }
  return Pair(d1, d2)
}

private fun playRecursiveCombat(myDeck: Deck, crabDeck: Deck): Result {
  val d1 = myDeck.toMutableList()
  val d2 = crabDeck.toMutableList()

  val playedGames = mutableSetOf<Pair<Int, Int>>()
  while (d1.isNotEmpty() && d2.isNotEmpty()) {
    val sig = deckSignature(d1, d2)
    if (playedGames.contains(sig)) {
      return Result(true, d1, d2)
    }
    playedGames.add(sig)
    val c1 = d1.removeAt(0)
    val c2 = d2.removeAt(0)

    val isD1Winner =
        if (c1 > d1.size || c2 > d2.size) c1 > c2
        else playRecursiveCombat(d1.subList(0,c1), d2.subList(0,c2)).isDeck1Win
    if (isD1Winner) {
      d1.add(c1)
      d1.add(c2)
    } else {
      d2.add(c2)
      d2.add(c1)
    }
  }
  return Result(d1.isNotEmpty(), d1, d2)
}

fun deckSignature(d1: Deck, d2: Deck): Pair<Int, Int> = Pair(d1.hashCode(), d2.hashCode())

data class Result(val isDeck1Win: Boolean, val d1: Deck, val d2: Deck)

typealias Deck = List<Int>
