package adventofcode.`2019`.`22`

import java.io.File

/**
 * Solution for [Advent of Code day 22](https://adventofcode.com/2019/day/22).
 */
fun main() {
    // Read input file
    val shuffles =
        File("src/adventofcode/2019/22/input.txt").readLines()

    // Part 1
    val deckSize = 10007
    var deck = List(deckSize) { it }
    for (shuffle in shuffles) {
        deck = when {
            shuffle.startsWith("cut ") ->
                deck.cut(shuffle.split(" ").last().toInt())
            shuffle.startsWith("deal into") ->
                deck.intoNewStack()
            else ->
                deck.dealWithIncrement(shuffle.split(" ").last().toInt())
        }
    }
    println(deck.indexOf(2019))

    // Part 2
    val bigDeckSize = 119315717514047
    val bigDeckShuffleIters = 101741582076661
}

fun <T> List<T>.intoNewStack(): List<T> {
    return this.reversed()
}

fun <T> List<T>.cut(n: Int): List<T> {
    val cutLength = if (n < 0) this.size + n else n
    return this.takeLast(this.size - cutLength) + this.subList(0, cutLength)
}

inline fun <reified T : Any> List<T>.dealWithIncrement(n: Int): List<T> {
    val shuffledDeck = arrayOfNulls<T>(this.size)
    var j = 0
    for (i in this.indices) {
        shuffledDeck[j % this.size] = this[i]
        j += n
    }
    return shuffledDeck.filterNotNull().toList()
}