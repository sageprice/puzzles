package adventofcode.`2019`.`07`

import adventofcode.`2019`.intcode.evalSpec
import adventofcode.`2019`.intcode.HaltOp
import adventofcode.`2019`.intcode.InputOp
import adventofcode.`2019`.intcode.Mode
import adventofcode.`2019`.intcode.Instruction
import java.io.File

/**
 * Solution for [Advent of Code day 7](https://adventofcode.com/2019/day/7).
 */
fun main() {
    // Read input file
    val intcode: List<Long> =
        File("src/adventofcode/2019/07/input.txt")
            .readLines()[0]
            .split(",")
            .map { it.toLong() }

    // Part 1
    var maxSignal = 0L
    for (input in generateInputPermutations(0..4)) {
        val amp1 = evalSpec(intcode, listOf(input[0].toLong(), 0)).first
        val amp2 = evalSpec(intcode, listOf(input[1].toLong(), amp1)).first
        val amp3 = evalSpec(intcode, listOf(input[2].toLong(), amp2)).first
        val amp4 = evalSpec(intcode, listOf(input[3].toLong(), amp3)).first
        val amp5 = evalSpec(intcode, listOf(input[4].toLong(), amp4)).first
        if (maxSignal < amp5) {
            maxSignal = amp5
        }
    }
    println("Maximum signal without feedback is: $maxSignal")

    // Part 2
    maxSignal = 0
    for (input in generateInputPermutations(5..9)) {
        var ultimate = 0L
        var exitCode: Instruction = InputOp(Mode.IMMEDIATE)
        val startIndices = mutableListOf(0, 0, 0, 0, 0)
        val feedbackInputs = mutableListOf<MutableList<Long>>(
            mutableListOf(input[0].toLong(), 0),
            mutableListOf(input[1].toLong()),
            mutableListOf(input[2].toLong()),
            mutableListOf(input[3].toLong()),
            mutableListOf(input[4].toLong()))
        while (exitCode != HaltOp) {
            val amp1 = evalSpec(intcode, feedbackInputs[0], startIndices[0])
            startIndices[0] = amp1.second
            feedbackInputs[1].add(amp1.first)
            val amp2 = evalSpec(intcode, feedbackInputs[1], startIndices[1])
            startIndices[1] = amp2.second
            feedbackInputs[2].add(amp2.first)
            val amp3 = evalSpec(intcode, feedbackInputs[2], startIndices[2])
            startIndices[2] = amp3.second
            feedbackInputs[3].add(amp3.first)
            val amp4 = evalSpec(intcode, feedbackInputs[3], startIndices[3])
            startIndices[3] = amp4.second
            feedbackInputs[4].add(amp4.first)
            val amp5 = evalSpec(intcode, feedbackInputs[4], startIndices[4])
            startIndices[4] = amp5.second
            ultimate = amp5.first
            exitCode = amp5.third
            feedbackInputs[0] = mutableListOf(amp5.first)
            feedbackInputs[1] = mutableListOf()
            feedbackInputs[2] = mutableListOf()
            feedbackInputs[3] = mutableListOf()
            feedbackInputs[4] = mutableListOf()
        }
        maxSignal = maxOf(maxSignal, ultimate)
    }
    println("Maximum signal running in feedback mode is: $maxSignal")
}

/** lolololololololololol */
private fun generateInputPermutations(inputRange: IntRange): List<List<Int>> {
    val validInputs = mutableListOf<List<Int>>()
    for (i in inputRange) {
        val inputs = mutableListOf<Int>(i)
        for (j in inputRange) {
            if (!inputs.contains(j)) {
                inputs.add(j)
                for (k in inputRange) {
                    if (!inputs.contains(k)) {
                        inputs.add(k)
                        for (l in inputRange) {
                            if (!inputs.contains(l)) {
                                inputs.add(l)
                                for (m in inputRange) {
                                    if (!inputs.contains(m)) {
                                        inputs.add(m)
                                        validInputs.add(inputs.toList())
                                        inputs.remove(m)
                                    }
                                }
                                inputs.remove(l)
                            }
                        }
                        inputs.remove(k)
                    }
                }
                inputs.remove(j)
            }
        }
    }
    return validInputs
}