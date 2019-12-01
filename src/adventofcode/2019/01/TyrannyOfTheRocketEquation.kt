package adventofcode.`2019`.`01`

import java.io.File

/** Solution for [Advent of Code day 1](https://adventofcode.com/2019/day/1).*/
fun main() {
    // Read input file
    val moduleMasses: List<Int> =
        File("src/adventofcode/2019/01/input.txt")
            .readLines()
            .map { it.toInt() }
    // Part 1:
    val moduleFuel = moduleMasses.map { it / 3 - 2 }.sum()
    println("We need $moduleFuel fuel to transport our modules.")

    // Part 2:
    val betterEstimate =
        moduleMasses.map { recursivelyCalculateFuelMass(it) }.sum()
    println("But if we account for fuel needing fuel, we actually need $betterEstimate")
}

private fun recursivelyCalculateFuelMass(mass: Int): Int {
    var requiredFuel = 0
    var incrementalFuel = mass
    while (incrementalFuel > 8) { // 8 = point at which next step < 0
        val inc = (incrementalFuel / 3) - 2
        requiredFuel += inc
        incrementalFuel = inc
    }
    return requiredFuel
}