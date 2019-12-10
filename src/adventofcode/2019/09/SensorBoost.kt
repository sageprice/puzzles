package adventofcode.`2019`.`09`

import adventofcode.`2019`.intcode.evalSpec
import java.io.File

/**
 * Solution for [Advent of Code day 9](https://adventofcode.com/2019/day/9).
 */
fun main() {
    // Read input file
    val intcodeProgram: List<Long> =
            File("src/adventofcode/2019/09/input.txt")
                    .readLines()[0]
                    .split(",")
                    .map { it.toLong() }
    println("Part 1: ${evalSpec(intcodeProgram, inputs = listOf(1)).first}")
    println("Part 2: ${evalSpec(intcodeProgram, inputs = listOf(2)).first}")
}