package adventofcode.`2019`.`16`

import java.io.File
import kotlin.math.absoluteValue

fun main() {
    // Read input file
    val rules: IntArray =
        File("src/adventofcode/2019/16/input.txt")
            .readLines()[0].map { it.toString().toInt() }.toIntArray()

    // Part 1
    val tStart = System.currentTimeMillis()
    val basePattern = intArrayOf(0, 1, 0, -1)
    var result = rules
    for (i in 1..100) {
//        print("$i:\t")
//        result.forEach { print(it) }
        result = transform(result, basePattern)
//        println()
    }
    println(System.currentTimeMillis() - tStart)

    println("Part 1: ${(0..7).map { result[it].toString() }.reduce { a, b -> a + b }}")

    // Part 2
}

fun transform(input: IntArray, pattern: IntArray): IntArray {
    val outputs = Array(input.size) { 0 }.toIntArray()

    for (i in input.indices) {
//        println("    ---  Inner loop $i")
        val positionRepeats = pattern.mapIndexed { _, v -> List(i + 1) { v } }.flatten()
        var repeatedPattern = positionRepeats
        while (repeatedPattern.size < input.size + 1) {
            repeatedPattern = repeatedPattern + repeatedPattern
        }
        val nextDigit = input.indices.map { idx ->
            input[idx] * repeatedPattern[idx + 1]
        }.sum().absoluteValue % 10
//        val nextDigit =
//            input
//                .zip(repeatedPattern)
//                .map { it.first * it.second }
//                .sum().absoluteValue % 10
        outputs[i] = nextDigit
//        println(outputs)
    }
    return outputs
}

fun nextMultiplier(pattern: Array<Int>, idx: Int, loopNum: Int): Int {
    return 1
}