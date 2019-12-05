package adventofcode.`2019`.`05`

import java.io.File

/**
 * Solution for [Advent of Code day 5](https://adventofcode.com/2019/day/5).
 */
fun main() {
  // Read input file
  val programSpec: List<Int> =
      File("input.txt")
      // File("test_input.txt")
         .readLines().get(0).split(",").map { it.toInt() }

  // Part 1 -- provide input 1
  // Part 2 -- provide input 5
  evalSpec(programSpec)
}

fun evalSpec(program: List<Int>): Int {
  val prog: MutableList<Int> = program.toMutableList()
  var programIndex: Int = 0
  var instr = parseInstruction(prog[programIndex])
  while (instr != HaltOp) {
    when (instr) {
      is InputOp -> {
        print("Please provide an integer input: ")
        val input = readLine()
        val position = prog[programIndex + 1]
        prog[position] = 
            input?.toInt() ?: throw Exception("What is this? $input")
        programIndex += 2
      }
      is OutputOp -> {
        val arg = prog[programIndex + 1]
        val output = if (instr.outMode == Mode.IMMEDIATE) arg else prog[arg]
        println("$programIndex -- output at index $arg: $output")
        programIndex += 2
      }
      is UnaryInstruction -> {
        val first = if (instr.inMode == Mode.POSITION) {
              prog[prog[programIndex + 1]]
            } else {
              prog[programIndex + 1]
            }
        val second = if (instr.outMode == Mode.POSITION) {
              prog[prog[programIndex + 2]]
            } else {
              prog[programIndex + 2]
            }
        when (instr.action) {
          UnaryAction.JUMP_IF_TRUE -> 
              programIndex = if (first != 0) second else programIndex + 3
          UnaryAction.JUMP_IF_FALSE -> 
              programIndex = if (first == 0) second else programIndex + 3
        }
      }
      is BinaryInstruction -> {
        val first = if (instr.firstInMode == Mode.POSITION) {
          prog[prog[programIndex + 1]]
        } else {
          prog[programIndex + 1]
        }
        val second = if (instr.secondInMode == Mode.POSITION) {
          prog[prog[programIndex + 2]]
        } else {
          prog[programIndex + 2]
        }
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
  return prog.get(0)
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
