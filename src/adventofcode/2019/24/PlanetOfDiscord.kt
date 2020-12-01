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
            .map { it.toCharArray() }
            .toTypedArray()

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

    // Part 2
    val base = eris.clone()
    base[2][2] = '?'
    var erises = mapOf(0 to base)
    for (i in 1..200) {
        erises = nextWorld(erises)
    }
    println("Part 2: " +
            "${erises
                .map { it.value }
                .map { it.map { it.count { it == '#' } }.sum() }
                .sum()}")
}

fun nextWorld(erises: Map<Int, Array<CharArray>>): Map<Int, Array<CharArray>> {
    val newErises = mutableMapOf<Int, Array<CharArray>>()
    for (e in erises) {
        val i = e.key
        val m = e.value
        val e1 = recurseLevel(erises[i+1], m, erises[i-1])
        newErises[i] = e1
    }
    val eLow = erises.keys.min()!!
    var newLow = Array(5) { CharArray(5) { '.' } }
    newLow[2][2] = '?'
    newLow = recurseLevel(erises[eLow], newLow, null)
    if (newLow.any { it.any { it == '#' } } ) newErises[eLow - 1] = newLow
    val eHigh = erises.keys.max()!!
    var newHigh = Array(5) { CharArray(5) { '.' } }
    newHigh[2][2] = '?'
    newHigh = recurseLevel(null, newHigh, erises[eHigh])
    if (newHigh.any { it.any { it == '#' } } ) newErises[eHigh + 1] = newHigh
    return newErises
}

fun recurseLevel(
    up: Array<CharArray>?,
    c: Array<CharArray>,
    down: Array<CharArray>?
): Array<CharArray> {
    val c1 = Array(c.size) { CharArray(c[0].size) { '.' } }

    for (i in c1.indices) {
//        println()
//        c1.prettyPrint()
        for (j in c1[0].indices) {
            if (i == 2 && j == 2) continue
            var adjacentBugs = 0
            //      up
            if (i-1 in c.indices) {
                if (c[i - 1][j] == '#') adjacentBugs++
                else if (c[i - 1][j] == '?' && down != null)
                    adjacentBugs += down.last().count { it == '#' }
            }
            else {
                    if (up != null && up[1][2] == '#') {
                        adjacentBugs++
                    }
            }

            //      down
            if (i+1 in c.indices) {
                if (c[i + 1][j] == '#') adjacentBugs++
                else if (c[i + 1][j] == '?' && down != null)
                    adjacentBugs += down.first().count { it == '#' }
            } else {
                if (up != null && up[3][2] == '#') {
                    adjacentBugs++
                }
            }

            //      left
            if (j-1 in c[0].indices) {
                if (c[i][j - 1] == '#') adjacentBugs++
                else if (c[i][j - 1] == '?' && down != null)
                    adjacentBugs += down.map { it.last() }.count { it == '#' }
            } else {
                if (up != null && up[2][1] == '#') {
                    adjacentBugs++
                }
            }

            //      right
            if (j+1 in c[0].indices) {
                if (c[i][j + 1] == '#') adjacentBugs++
                else if (c[i][j + 1] == '?' && down != null)
                    adjacentBugs += down.map { it.first() }.count { it == '#' }
            } else {
                if (up != null && up[2][3] == '#') {
                    adjacentBugs++
                }
            }

            c1[i][j] = when {
                c[i][j] == '#' && adjacentBugs == 1 -> '#'
                c[i][j] == '#' && adjacentBugs != 1 -> '.'
                c[i][j] == '.' && adjacentBugs in 1..2 -> '#'
                else -> c[i][j]
            }
        }
    }
    c1[2][2] = '?'
    return c1
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