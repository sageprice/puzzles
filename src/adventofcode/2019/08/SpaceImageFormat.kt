package adventofcode.`2019`.`08`

import java.io.File

/**
 * Solution for [Advent of Code day 8](https://adventofcode.com/2019/day/8).
 */
fun main() {
    // Read input file
    val image: String =
        File("src/adventofcode/2019/08/input.txt").readLines()[0]
    val imgWidth = 25
    val imgHeight = 6

    // Part 1
    val layers = image.chunked(imgHeight * imgWidth)
    val zeroCountToOneTwoCountsProduct =
        layers.map {
            Pair(
                it.charCount('0'),
                it.charCount('1') * it.charCount('2')) }
    val result = zeroCountToOneTwoCountsProduct.sortedBy { it.first }[0].second
    println("Part 1 answer: $result")

    // Part 2
    val out = layers.reduce { a, b ->
        (a.indices)
            .map { if (a[it] == '2') b[it] else a[it] }
            .joinToString("")
    }
    println("Part 2 answer, good luck reading...")
    // Prints out a hard to read code... 1s form letters
    out.chunked(imgWidth).forEach { println(it) }
}

fun String.charCount(char: Char): Int {
    return this.count { c -> c == char }
}