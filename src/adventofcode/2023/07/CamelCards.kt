package adventofcode.`2023`.`07`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2023/07/input.txt").readLines()
  val hands = input.map { parseHand(it) }

  val sorted = hands.sortedWith(handComparator())
  println(sorted.indices.sumOf { (it+1) * sorted[it].bid })

  val jokerHands = input.map { parseJokerHand(it) }
  val sortedJokers = jokerHands.sortedWith(handComparator())
  println(sortedJokers.indices.sumOf { (it+1) * sortedJokers[it].bid })
}

private fun handComparator() = Comparator<Hand> { a, b ->
  when {
    a.type < b.type -> -1
    a.type > b.type -> 1
    else -> {
      val (l, r) = a.cards.zip(b.cards).first { (x, y) -> x != y }
      when  {
        l < r -> -1
        l > r -> 1
        else -> 0
      }
    }
  }
}

private fun parseJokerHand(str: String): Hand {
  val (cs, bid) = str.split(" ")
  val cards = cs.map { JOKER_MAP[it]!! }
  val grouped = cards.groupBy { it }.mapValues { it.value.size }
  val jokerCount = grouped[Card.JOKER] ?: 0
  val withoutJokers = grouped.filter { it.key != Card.JOKER }
  val mostCommonCount = (if (withoutJokers.isEmpty()) 0 else withoutJokers.values.max()) + jokerCount
  val type = when {
    mostCommonCount == 5 -> HandType.FIVE_OF_A_KIND
    mostCommonCount == 4 -> HandType.FOUR_OF_A_KIND
    mostCommonCount == 3 && withoutJokers.values.min() == 2 -> HandType.FULL_HOUSE
    mostCommonCount == 3 -> HandType.THREE_OF_A_KIND
    mostCommonCount == 2 && withoutJokers.keys.distinct().size == 3 -> HandType.TWO_PAIR
    mostCommonCount == 2 -> HandType.PAIR
    else -> HandType.HIGH_CARD
  }
  return Hand(cards, type, bid.toLong())
}

private fun parseHand(str: String): Hand {
  val (cs, bid) = str.split(" ")
  val cards = cs.map { CARD_MAP[it]!! }
  val grouped = cards.groupBy { it }.mapValues { it.value.size }
  val mostCommonCount = grouped.values.max()
  val type = when {
    mostCommonCount == 5 -> HandType.FIVE_OF_A_KIND
    mostCommonCount == 4 -> HandType.FOUR_OF_A_KIND
    mostCommonCount == 3 && grouped.values.min() == 2 -> HandType.FULL_HOUSE
    mostCommonCount == 3 -> HandType.THREE_OF_A_KIND
    mostCommonCount == 2 && grouped.keys.distinct().size == 3 -> HandType.TWO_PAIR
    mostCommonCount == 2 -> HandType.PAIR
    else -> HandType.HIGH_CARD
  }
  return Hand(cards, type, bid.toLong())
}

private data class Hand(
  val cards: List<Card>,
  val type: HandType,
  val bid: Long
)

private enum class HandType {
  HIGH_CARD,
  PAIR,
  TWO_PAIR,
  THREE_OF_A_KIND,
  FULL_HOUSE,
  FOUR_OF_A_KIND,
  FIVE_OF_A_KIND
}

private val JOKER_MAP: Map<Char, Card> = mapOf(
  Pair('J', Card.JOKER),
  Pair('2', Card.TWO),
  Pair('3', Card.THREE),
  Pair('4', Card.FOUR),
  Pair('5', Card.FIVE),
  Pair('6', Card.SIX),
  Pair('7', Card.SEVEN),
  Pair('8', Card.EIGHT),
  Pair('9', Card.NINE),
  Pair('T', Card.TEN),
  Pair('Q', Card.QUEEN),
  Pair('K', Card.KING),
  Pair('A', Card.ACE)
)

private val CARD_MAP: Map<Char, Card> = mapOf(
  Pair('2', Card.TWO),
  Pair('3', Card.THREE),
  Pair('4', Card.FOUR),
  Pair('5', Card.FIVE),
  Pair('6', Card.SIX),
  Pair('7', Card.SEVEN),
  Pair('8', Card.EIGHT),
  Pair('9', Card.NINE),
  Pair('T', Card.TEN),
  Pair('J', Card.JACK),
  Pair('Q', Card.QUEEN),
  Pair('K', Card.KING),
  Pair('A', Card.ACE)
)

private enum class Card {
  JOKER,
  TWO,
  THREE,
  FOUR,
  FIVE,
  SIX,
  SEVEN,
  EIGHT,
  NINE,
  TEN,
  JACK,
  QUEEN,
  KING,
  ACE
}