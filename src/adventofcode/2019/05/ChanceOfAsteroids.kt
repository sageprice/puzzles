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

  // Part 1
  evalSpec(programSpec)
}

fun evalSpec(spec: List<Int>): Int {
  val mutSpec: MutableList<Int> = spec.toMutableList()
  var programIndex: Int = 0
  var instr = parseInstruction(mutSpec[programIndex])
  while (instr != HaltOp) {
    // println(instr)
    when (instr) {
      is InputOp -> {
        print("Please provide an integer input: ")
        val input = readLine()
        val position = mutSpec[programIndex + 1]
        mutSpec[position] = input?.toInt() ?: throw Exception("What is this? $input")
        programIndex += 2
      }
      is OutputOp -> {
        val arg = mutSpec[programIndex + 1]
        // println(mutSpec.slice(0..programIndex))
        print("$programIndex -- ")
        val output = if (instr.outMode == Mode.IMMEDIATE) arg else mutSpec[arg]
        println("Output at index $arg: $output")
        programIndex += 2
      }
      is BinaryOp -> {
        val first = if (instr.firstInMode == Mode.POSITION) {
          mutSpec[mutSpec[programIndex + 1]]
        } else {
          mutSpec[programIndex + 1]
        }
        val second = if (instr.secondInMode == Mode.POSITION) {
          mutSpec[mutSpec[programIndex + 2]]
        } else {
          mutSpec[programIndex + 2]
        }
        val third = mutSpec[programIndex + 3]
        // println("${mutSpec[programIndex]}, $first, $second, $third")
        when (instr.action) {
          Action.ADD -> mutSpec[third] = first + second
          Action.MULTIPLY -> mutSpec[third] = first * second
          else -> throw Exception("Not a valid binary op: ${instr.action}")
        }
        programIndex += 4
      }
      else -> throw Exception("How did we get here? $instr")
    }
    // println(mutSpec.slice(0..programIndex))
    instr = parseInstruction(mutSpec[programIndex])
  }
  return mutSpec.get(0)
}

fun parseInstruction(instr: Int): Instruction {
  val opcode = instr % 100
  val action = when (opcode) {
    1 -> Action.ADD
    2 -> Action.MULTIPLY
    3 -> return InputOp
    4 -> {
      val mode = if (instr / 100 == 1) Mode.IMMEDIATE else Mode.POSITION
      return OutputOp(mode)
    }
    99 -> return HaltOp
    else -> throw Exception("Cannot parse opcode $opcode from instruction $instr")
  }
  var instrCopy = instr / 100
  val mode1 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
  instrCopy /= 10
  val mode2 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
  instrCopy /= 10
  val mode3 = if (instrCopy % 10 == 1) Mode.IMMEDIATE else Mode.POSITION
  return BinaryOp(action, mode1, mode2, mode3)
}

enum class Action {
    ADD,
    MULTIPLY,
    INPUT,
    OUTPUT,
    HALT
}

enum class Mode {
    POSITION,
    IMMEDIATE
}

sealed class Instruction(open val action: Action)

data class BinaryOp(
    override val action: Action, val firstInMode: Mode, val secondInMode: Mode, val outMode: Mode
): Instruction(action)

object InputOp: Instruction(Action.INPUT)

data class OutputOp(val outMode: Mode): Instruction(Action.OUTPUT)

object HaltOp: Instruction(Action.HALT)