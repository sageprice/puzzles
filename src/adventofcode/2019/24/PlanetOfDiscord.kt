package adventofcode.`2019`.`24`

import java.io.File
import kotlin.math.pow

/**
 * Solution for [Advent of Code day 24](https://adventofcode.com/2019/day/24).
 */
fun main() {
    // Read input file
    val eris =
        File("src/adventofcode/2019/24/input.txt")
            .readLines()
            .map { it.toCharArray() }.toTypedArray()

    // Part 1
    var e0 = eris.clone()
    val layouts = mutableSetOf<String>()

    while (!layouts.contains(e0.serialize())) {
//        println("${layouts.size}===============")
//        e0.prettyPrint()
        layouts.add(e0.serialize())
        e0 = nextState(e0)
    }

    println("Part 1: ${e0.biodiversity()}")

}

fun nextState(s0: Array<CharArray>): Array<CharArray> {
    val s1 = Array(s0.size) { CharArray(s0[0].size) { '.' } }

    for (i in s1.indices) {
//        println()
//        s1.prettyPrint()
        for (j in s1[0].indices) {
            var adjacentBugs = 0
            if (i-1 in s1.indices && s0[i-1][j] == '#') adjacentBugs++
            if (i+1 in s1.indices && s0[i+1][j] == '#') adjacentBugs++
            if (j-1 in s1[0].indices && s0[i][j-1] == '#') adjacentBugs++
            if (j+1 in s1[0].indices && s0[i][j+1] == '#') adjacentBugs++
            s1[i][j] = when {
                s0[i][j] == '#' && adjacentBugs == 1 -> '#'
                s0[i][j] == '#' && adjacentBugs != 1 -> '.'
                s0[i][j] == '.' && adjacentBugs in 1..2 -> '#'
                else -> s0[i][j]
            }
        }
    }
    return s1
}

fun Array<CharArray>.serialize(): String {
    return this.map { it.concatToString() }.reduce { a, b -> a + b}
}

fun Array<CharArray>.biodiversity(): Long {
    return this.serialize()
        .mapIndexed { i, v -> if (v == '#') 2.0.pow(i).toLong() else 0L }
        .sum()
}

fun Array<CharArray>.prettyPrint() {
    for (i in this.indices) {
        println(this[i].concatToString())
    }
}