package adventofcode.`2019`.intcode

/**
 * Executes an intcode program, with the given inputs, starting from the provided index.
 *
 * Returns a tuple of <latest output, current execution instruction index, and the [Instruction] at that index.
 */
fun evalSpec(program: List<Int>, inputs: List<Int>? = null, startIndex: Int = 0): Triple<Int, Int, Instruction> {
    val prog: MutableList<Int> = program.toMutableList()
    var programIndex = startIndex
    var instr = parseInstruction(prog[programIndex])
    var inputIndex = 0
    var output: Int = -1
    while (instr != HaltOp) {
        when (instr) {
            is InputOp -> {
                val input: Int = if (inputs == null) {
                    print("Please provide an integer input: ")
                    readLine()!!.toInt()
                } else {
                    if (inputIndex < inputs.size) {
                        inputs[inputIndex++]
                    } else {
//                        println("Out of input, requesting further input. $output, $programIndex")
                        return Triple(output, programIndex, InputOp)
                    }
                }
                val position = prog[programIndex + 1]
                prog[position] = input
                programIndex += 2
            }
            is OutputOp -> {
                val arg = prog[programIndex + 1]
                output = if (instr.outMode == Mode.IMMEDIATE) arg else prog[arg]
//                println("$programIndex -- output at index $arg: $output")
                programIndex += 2
            }
            is UnaryInstruction -> {
                val first = getArgument(instr.inMode, prog, programIndex, 1)
                val second = getArgument(instr.outMode, prog, programIndex, 2)
                programIndex = when (instr.action) {
                    UnaryAction.JUMP_IF_TRUE ->
                        if (first != 0) second else programIndex + 3
                    UnaryAction.JUMP_IF_FALSE ->
                        if (first == 0) second else programIndex + 3
                }
            }
            is BinaryInstruction -> {
                val first = getArgument(instr.firstInMode, prog, programIndex, 1)
                val second = getArgument(instr.secondInMode, prog, programIndex, 2)
                val third = prog[programIndex + 3]
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
            else -> throw Exception("How did we get here? $instr")
        }
        instr = parseInstruction(prog[programIndex])
    }
//    println("Terminating: $output, $programIndex")
    return Triple(output, programIndex, HaltOp)
}

fun parseInstruction(instr: Int): Instruction {
    val opcode = instr % 100
    val action = when (opcode) {
        1 -> BinaryAction.ADD
        2 -> BinaryAction.MULTIPLY
        3 -> return InputOp
        4 -> {
            val mode = if (instr / 100 == 1) Mode.IMMEDIATE else Mode.POSITION
            return OutputOp(mode)
        }
        5 -> UnaryAction.JUMP_IF_TRUE
        6 -> UnaryAction.JUMP_IF_FALSE
        7 -> BinaryAction.LESS_THAN
        8 -> BinaryAction.EQUALS
        99 -> return HaltOp
        else ->
            throw Exception("Cannot parse opcode $opcode from instruction $instr")
    }
    var instrCopy = instr / 100
    if (action is UnaryAction) {
        val mode1 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
        instrCopy /= 10
        val mode2 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
        return UnaryInstruction(action, mode1, mode2)
    } else if (action is BinaryAction) {
        val mode1 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
        instrCopy /= 10
        val mode2 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
        instrCopy /= 10
        val mode3 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
        return BinaryInstruction(action, mode1, mode2, mode3)
    } else {
        throw Exception("Unsupported action: $action")
    }
}

/** Retrieves the argument at index + argNum following the given mode. */
fun getArgument(mode: Mode, prog: List<Int>, index: Int, argNum: Int): Int {
    return if (mode == Mode.POSITION) {
        prog[prog[index + argNum]]
    } else {
        prog[index + argNum]
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
    IMMEDIATE
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
object InputOp: Instruction()

/**
 * Special instruction to write to std out, specifying how the output source
 * should be read.
 */
data class OutputOp(val outMode: Mode): Instruction()

object HaltOp: Instruction()
