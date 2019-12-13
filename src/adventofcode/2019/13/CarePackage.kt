package adventofcode.`2019`.`13`

import java.io.File

fun main() {
    // Read input file
    val programSpec: List<Long> =
            File("src/adventofcode/2019/13/input.txt")
                    // File("test_input.txt")
                    .readLines()[0].split(",").map { it.toLong() }

    // Part 1
    val output1 = evalSpec(programSpec).outputs
    println("Part 1: there are ${output1.chunked(3).count { it[2] == 2L }} block tiles on exit")

    // Part 2
    val endlessQuarters = programSpec.toMutableList()
    endlessQuarters[0] = 2
    println("Part 2: final score is ${playArcade(endlessQuarters)}")
}

private fun playArcade(endlessQuarters: MutableList<Long>): Int {
    var execution = evalSpec(endlessQuarters, listOf())
    var score = 0
    while (execution.exitInstr != HaltOp) {
        val output = execution.outputs

        val triples = output.map { it.toInt() }.chunked(3)
        val scoreDisplayPair = triples.partition { it[0] == -1 }
        val scoreTriples = scoreDisplayPair.first
        if (scoreTriples.isNotEmpty()) {
            score = scoreTriples.last()[2]
//            println("New score: $score")
        }
        val tiles: List<List<Int>> = scoreDisplayPair.second

        val xMax = tiles.map { it[0] }.max()!!
        val yMax = tiles.map { it[1] }.max()!!

        var paddleX = -1
        var ballX = -1

        val gameDisplay = Array(yMax + 1) { Array(xMax + 1) { " " } }
        tiles.forEach {
            gameDisplay[it[1]][it[0]] = when (it[2]) {
                0 -> " "
                1 -> "■"
                2 -> "⬚"
                3 -> {
                    paddleX = it[0]
                    "-"
                }
                4 -> {
                    ballX = it[0]
                    "●"
                }
                else -> throw Exception("Unrecognized input ${it[2]}")
            }
        }
//        gameDisplay.forEach { line ->
//            line.forEach { print(it) }
//            println()
//        }
        // Always move the paddle in the direction of the ball
        val paddleInput = when {
            paddleX < ballX -> 1
            paddleX == ballX -> 0
            else -> -1
        }

        execution =
                evalSpec(
                        execution.prog,
                        listOf(paddleInput.toLong()),
                        execution.startIndex,
                        execution.relativeOffset,
                        true)
    }
    val output = execution.outputs

    val triples = output.map { it.toInt() }.chunked(3)
    val scoreDisplayPair = triples.partition { it[0] == -1 }
    val scoreTriples = scoreDisplayPair.first
    if (scoreTriples.isNotEmpty()) {
        score = scoreTriples.last()[2]
        println("New score: $score")
    }
    return score
}

/**
 * Executes an intcode program, with the given inputs, starting from the provided index.
 *
 * Returns a tuple of <latest output, current execution instruction index, and the [Instruction] at that index.
 */
fun evalSpec(
        program: List<Long>,
        inputs: List<Long>? = mutableListOf(1),
        startIndex: Int = 0,
        initialRelativeOffset: Int = 0,
        hasRam: Boolean = false): Execution {
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
//                        println("Out of input, requesting further input. $output, $programIndex")
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

fun parseInstruction(instr: Int): Instruction {
    val action = when (val opcode = instr % 100) {
        1 -> BinaryAction.ADD
        2 -> BinaryAction.MULTIPLY
        3 -> return InputOp(getMode(instr / 100))
        4 -> return OutputOp(getMode(instr / 100))
        5 -> UnaryAction.JUMP_IF_TRUE
        6 -> UnaryAction.JUMP_IF_FALSE
        7 -> BinaryAction.LESS_THAN
        8 -> BinaryAction.EQUALS
        9 -> return ModifyRelativeOffset(getMode(instr / 100))
        99 -> return HaltOp
        else ->
            throw Exception("Cannot parse opcode $opcode from instruction $instr")
    }
    var instrCopy = instr / 100
    return when (action) {
        is UnaryAction -> {
            val mode1 = getMode(instrCopy % 10)
            instrCopy /= 10
            val mode2 = getMode(instrCopy % 10)
            UnaryInstruction(action, mode1, mode2)
        }
        is BinaryAction -> {
            val mode1 = getMode(instrCopy % 10)
            instrCopy /= 10
            val mode2 = getMode(instrCopy % 10)
            instrCopy /= 10
            val mode3 = getMode(instrCopy % 10)
            BinaryInstruction(action, mode1, mode2, mode3)
        }
        else -> throw Exception("Unsupported action: $action")
    }
}

fun getMode(code: Int): Mode {
    return when (code % 10) {
        0 -> Mode.POSITION
        1 -> Mode.IMMEDIATE
        2 -> Mode.RELATIVE
        else -> throw Exception ("Unsupported opcode ${code % 10}")
    }
}

/** Retrieves the argument at index + argNum following the given mode. */
fun getArgument(mode: Mode, prog: List<Long>, index: Int, argNum: Int, relativeOffset: Int): Long {
    return when (mode) {
        Mode.POSITION -> {
            prog[prog[index + argNum].toInt()]
        }
        Mode.IMMEDIATE -> {
            prog[index + argNum]
        }
        Mode.RELATIVE -> {
            prog[prog[index + argNum].toInt() + relativeOffset]
        }
    }
}

/** Actions which take 1 input and produce 1 output. */
enum class UnaryAction {
    JUMP_IF_TRUE,
    JUMP_IF_FALSE
}

/** Actions which take 2 inputs and produce 1 output. */
enum class BinaryAction {
    ADD,
    MULTIPLY,
    LESS_THAN,
    EQUALS,
}

/** Modes by which a parameter value may be specified. */
enum class Mode {
    POSITION,
    IMMEDIATE,
    RELATIVE
}

/** Specifies an operation, and how to manage inputs and outputs to that op. */
sealed class Instruction

/** Specifies a [UnaryAction] and how to read/write inputs/outputs. */
data class UnaryInstruction(
        val action: UnaryAction, val inMode: Mode, val outMode: Mode
): Instruction()

/** Specifies a [BinaryAction] and how to read/write input/outputs. */
data class BinaryInstruction(
        val action: BinaryAction,
        val firstInMode: Mode,
        val secondInMode: Mode,
        val outMode: Mode
): Instruction()

/** Special instruction to consume std in, and write it to a location. */
data class InputOp(val outMode: Mode): Instruction()

/**
 * Special instruction to write to std out, specifying how the output source
 * should be read.
 */
data class OutputOp(val outMode: Mode): Instruction()

/**
 * Special instruction to modify the relative offset base. Always takes the
 * value of the argument immediately after.
 */
data class ModifyRelativeOffset(val mode: Mode): Instruction()

object HaltOp: Instruction()

data class Execution(
        val prog: List<Long>,
        val outputs: List<Long>,
        val startIndex: Int,
        val exitInstr: Instruction,
        val relativeOffset: Int)