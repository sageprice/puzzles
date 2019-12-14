package adventofcode.`2019`.`14`

import java.io.File
import kotlin.math.ceil

/**
 * Solution for [Advent of Code day 14](https://adventofcode.com/2019/day/14).
 */
fun main() {
    // Read input file
    val rules: List<List<String>> =
        File("src/adventofcode/2019/14/input.txt")
            .readLines().map { it.split(" => ") }

    val output =
        rules.map { it[1].split(" ") }
            .map { Output(name = it[1], units = it[0].toInt()) }

    val inputs =
        rules.map { f ->
            f[0]
                .split(", ")
                .map {
                    val p = it.split(" ")
                    Input(name = p[1], units = p[0].toInt()) } }

    val formulas = inputs.zip(output).map { Formula(it.first, it.second) }
    val lookups = formulas.associateBy { it.output.name }

    // Part 1
    val required = mapOf<String, Int>("FUEL" to 1)
    val slop = mapOf<String, Int>()
    val singleFuel =
        deriveOreCost(required.toMutableMap(), slop.toMutableMap(), lookups)
    val costOfSingleFuel =
        singleFuel.first["ORE"] ?: error("WHERE IS MY OREEEE??? $singleFuel")
    println("Part 1: producing a single unit of fuel costs $costOfSingleFuel units of ore")

    // Part 2
    var ore = 1_000_000_000_000
    var s = mapOf<String, Int>()
    var fuelProduced = 0
    while (ore > 0) {
        // Moving in big jumps to speed things along...
        // If there is a lot of production slop this will be very inaccurate.
        // Will require tuning for some inputs.
        val targetFuelProduction =
            when {
                ore > 10_000 * costOfSingleFuel -> 1000
                ore > 1_000 * costOfSingleFuel -> 100
                else -> 1
            }
        val production =
            deriveOreCost(mutableMapOf("FUEL" to targetFuelProduction), s.toMutableMap(), lookups)
        ore -= production.first["ORE"]!!
        fuelProduced += targetFuelProduction
        s = production.second
    }

    println("Ore left over (or excess simulated): $ore")
    println("Simulated fuel produced: $fuelProduced")
    println("Actual fuel produced: ${fuelProduced - 1} <--- that's the answer bud")
    println("Remaining slop: $s")
    println("Excess slop works out to " +
            "${s
                .map { 
                    deriveOreCost(
                        mutableMapOf(it.key to it.value), 
                        mutableMapOf(), 
                        lookups).first["ORE"]!! }
                .sum()} " +
            "units of ore")
}

fun deriveOreCost(
    required: MutableMap<String, Int>,
    slop: MutableMap<String, Int>,
    lookups: Map<String, Formula>
): Pair<Map<String, Int>, Map<String, Int>> {
    while (required.keys != setOf("ORE")) {
        val ingredient = required.keys.random() // Maybe use queue?
        if (ingredient == "ORE") continue

        val amount = required[ingredient]?.toDouble() ?: throw Exception("huh wat")
        if (amount.toInt() == 0) {
            required.remove(ingredient)
            continue
        }
        val formula = lookups[ingredient] ?: throw Exception("What the fuck is $ingredient")
        val n = ceil(amount / formula.output.units).toInt()
        val ingredientExcess: Int = n * formula.output.units - amount.toInt()
        slop[ingredient] = ingredientExcess
        formula.inputs.forEach { i ->
            var needed = n * i.units
            if (slop.containsKey(i.name)) {
                val sN = slop[i.name]!!
                if (sN >= needed) {
                    slop.computeIfPresent(i.name) { n, v -> v - needed }
                    needed = 0
                } else {
                    needed -= slop[i.name]!!
                    slop.remove(i.name)
                }
            }
            if (needed > 0) {
                if (required.containsKey(i.name)) {
                    required[i.name] = required[i.name]!! + needed
                } else {
                    required[i.name] = needed
                }
            }
        }
        required.remove(ingredient)
    }
    return Pair(required, slop.filterValues { it > 0 })
}

data class Output(val name: String, val units: Int)
data class Input(val name: String, val units: Int)

data class Formula(val inputs: List<Input>, val output: Output)