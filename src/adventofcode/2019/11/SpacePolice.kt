package adventofcode.`2019`.`11`

import adventofcode.`2019`.intcode.BinaryAction
import adventofcode.`2019`.intcode.BinaryInstruction
import adventofcode.`2019`.intcode.getArgument
import adventofcode.`2019`.intcode.parseInstruction
import adventofcode.`2019`.intcode.HaltOp
import adventofcode.`2019`.intcode.InputOp
import adventofcode.`2019`.intcode.Instruction
import adventofcode.`2019`.intcode.Mode
import adventofcode.`2019`.intcode.ModifyRelativeOffset
import adventofcode.`2019`.intcode.OutputOp
import adventofcode.`2019`.intcode.UnaryAction
import adventofcode.`2019`.intcode.UnaryInstruction
import java.io.File

/**
 * Solution for [Advent of Code day 11](https://adventofcode.com/2019/day/11).
 */
fun main() {
    // Read input file
    val programSpec: List<Long> =
            File("src/adventofcode/2019/11/input.txt")
                    // File("test_input.txt")
                    .readLines()[0].split(",").map { it.toLong() }

    // Part 1
    val world = runAnt(programSpec, 0)
    println(world.size)

    // Part 2 -- Prints puzzle sideways but IDC
    val actualLicense = runAnt(programSpec, 1)
    val loX = actualLicense.keys.map { it.first }.min()!!
    val hiX = actualLicense.keys.map { it.first }.max()!!
    val loY = actualLicense.keys.map { it.second }.min()!!
    val hiY = actualLicense.keys.map { it.second }.max()!!
    val cleanLicense = actualLicense.mapKeys { Pair(it.key.first - loX, it.key.second - loY) }
    val license = Array(hiX - loX + 1) { Array(hiY - loY + 1) { " " } }
    for (key in cleanLicense.keys) {
        if (cleanLicense[key] == 1) license[key.first][key.second] =  "#"
    }
    for (line in license) {
        line.forEach { print(it) }
        println()
    }
}

fun runAnt(program: List<Long>, input: Int): Map<Pair<Int, Int>, Int> {
    val world = mutableMapOf<Pair<Int, Int>, Int>()

    var position = Pair(0, 0)
    println(position)
    var direction = Orientation.UP
    var execution = paintHull(program, mutableListOf(input.toLong()))
    while (execution.exitInstr != HaltOp) {
        val output = execution.outputs.map { it.toInt() }
        world[position] = output[0]
        direction = direction.turn(output[1])
        position = step(position, direction)
        println(position)
        // Resume execution
        if (execution.exitInstr is InputOp) {
            val nextInput: Int = world[position] ?: 0
            println("Re-starting turtle with input: $nextInput")
            execution =
                    paintHull(
                            execution.prog,
                            listOf(nextInput.toLong()),
                            execution.startIndex,
                            execution.relativeOffset,
                            hasRam = true)
        }
    }
    if (execution.outputs.isNotEmpty()) {
        val output = execution.outputs.map { it.toInt() }
        world[position] = output[0]
        direction = direction.turn(output[1])
        position = step(position, direction)
        println("Final position: $position")
    }
    return world
}

enum class Orientation {
    UP,
    RIGHT,
    DOWN,
    LEFT;

    fun turn(i: Int): Orientation {
        return if (i == 0) turnLeft() else turnRight()
    }

    private fun turnLeft(): Orientation {
        return when (this) {
            UP -> LEFT
            LEFT -> DOWN
            DOWN -> RIGHT
            RIGHT -> UP
        }
    }
    private fun turnRight(): Orientation {
        return when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }
}

fun step(position: Pair<Int, Int>, orientation: Orientation): Pair<Int, Int> {
    return when (orientation) {
        Orientation.UP -> Pair(position.first, position.second + 1)
        Orientation.RIGHT -> Pair(position.first + 1, position.second)
        Orientation.LEFT -> Pair(position.first - 1, position.second)
        Orientation.DOWN -> Pair(position.first, position.second - 1)
    }
}

/** Modififed intcode computer which executes a program to run a hull-painting robot. */
fun paintHull(
        program: List<Long>,
        inputs: List<Long>? = mutableListOf(1),
        startIndex: Int = 0,
        initialRelativeOffset: Int = 0,
        hasRam: Boolean = false
): Execution {
    val prog: MutableList<Long> = program.toMutableList()
    if (!hasRam) prog.addAll(generateSequence { 0L }.take(3000))
    var programIndex = startIndex
    var instr = parseInstruction(prog[programIndex].toInt())
    var inputIndex = 0
    var relativeOffset = initialRelativeOffset
    val outputs = mutableListOf<Long>()
    while (instr != HaltOp) {
        when (instr) {
            is InputOp -> {
//                println("Input instruction: $instr, " +
//                        "programIndex of $programIndex, " +
//                        "relative base offset of $relativeOffset, " +
//                        "base offset of ${prog[programIndex+1]}")
                val input: Long = if (inputs == null) {
                    print("Please provide an integer input: ")
                    readLine()!!.toLong()
                } else {
                    if (inputIndex < inputs.size) {
                        inputs[inputIndex++]
                    } else {
                        println("Out of input, requesting further input. $outputs, $programIndex")
                        return Execution(prog, outputs, programIndex, instr, relativeOffset)
                    }
                }
                // Special handling for write bit.
                val position = prog[programIndex + 1].toInt() + if (instr.outMode == Mode.RELATIVE) relativeOffset else 0
                prog[position] = input
                programIndex += 2
            }
            is OutputOp -> {
                val output = getArgument(instr.outMode, prog, programIndex, 1, relativeOffset)
//                println("$programIndex: $output")
                outputs.add(output)
                programIndex += 2
            }
            is UnaryInstruction -> {
                val first = getArgument(instr.inMode, prog, programIndex, 1, relativeOffset)
                val second = getArgument(instr.outMode, prog, programIndex, 2, relativeOffset).toInt()
                programIndex = when (instr.action) {
                    UnaryAction.JUMP_IF_TRUE ->
                        if (first != 0L) second else programIndex + 3
                    UnaryAction.JUMP_IF_FALSE ->
                        if (first == 0L) second else programIndex + 3
                }
            }
            is BinaryInstruction -> {
                val first = getArgument(instr.firstInMode, prog, programIndex, 1, relativeOffset)
                val second = getArgument(instr.secondInMode, prog, programIndex, 2, relativeOffset)
                // Special handling for write bit.
                val third = prog[programIndex + 3].toInt() + if (instr.outMode == Mode.RELATIVE) relativeOffset else 0
                when (instr.action) {
                    BinaryAction.ADD -> {
                        prog[third] = first + second
                    }
                    BinaryAction.MULTIPLY -> {
                        prog[third] = first * second
                    }
                    BinaryAction.LESS_THAN -> {
                        prog[third] = if (second > first) 1 else 0
                    }
                    BinaryAction.EQUALS -> {
                        prog[third] = if (second == first) 1 else 0
                    }
                }
                programIndex += 4
            }
            is ModifyRelativeOffset -> {
                relativeOffset += getArgument(instr.mode, prog, programIndex, 1, relativeOffset).toInt()
                programIndex += 2
            }
            else -> throw Exception("How did we get here? $instr")
        }
        instr = parseInstruction(prog[programIndex].toInt())
    }
//    println("Terminating: $output, $programIndex")
    return Execution(prog, outputs, programIndex, HaltOp, relativeOffset)
}

data class Execution(val prog: List<Long>, val outputs: List<Long>, val startIndex: Int, val exitInstr: Instruction, val relativeOffset: Int)