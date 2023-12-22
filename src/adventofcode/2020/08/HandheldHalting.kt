package adventofcode.`2020`.`08`

import java.io.File

fun main() {
  val program =
      File("src/adventofcode/2020/08/input.txt")
          .readLines()
          .map { toInstruction(it) }

  // Part 1
  println("Part 1: " + executeUntilLoop(program).second)

  // Part 2
  println("Part 2: " + executeUntilCompletion(program))
}

fun executeUntilCompletion(instrs: List<Instruction>): Int {
  for (i in instrs.indices) {
    val modifiedInstrs = instrs.toMutableList()
    if (instrs[i] is Acc) continue
    when (val ogInstr = instrs[i]) {
      is NoOp -> {
        modifiedInstrs[i] = Jump(ogInstr.value)
      }
      is Jump -> {
        modifiedInstrs[i] = NoOp(ogInstr.value)
      }
      else -> {}
    }
    val result = executeUntilLoop(modifiedInstrs)
    if (result.first) return result.second
  }
  error("Could not find correct instruction to flip")
}

// Returns the final value of the accumulator at program end or when a loop is found.
// The first value is True IFF the program executes to completion.
fun executeUntilLoop(instrs: List<Instruction>): Pair<Boolean, Int> {
  var acc = 0
  var index = 0
  val indicesExecuted = mutableListOf<Int>()
  while (!indicesExecuted.contains(index) && index != instrs.size) {
    indicesExecuted.add(index)
    when (val instr = instrs[index]) {
      is NoOp -> index++
      is Acc -> {
        acc += instr.value
        index++
      }
      is Jump -> index += instr.value
    }
  }
  return Pair(index == instrs.size, acc)
}

// TODO: extract to class if needed in future days.
fun toInstruction(instr: String): Instruction {
  val components = instr.split(" ")
  val value = components[1].toInt()
  return when (components[0]) {
    "acc" -> Acc(value)
    "nop" -> NoOp(value)
    "jmp" -> Jump(value)
    else -> error("Cannot parse instruction: $instr")
  }
}

// TODO: extract to class if needed in future days.
sealed class Instruction
data class NoOp(val value: Int): Instruction()
data class Acc(val value: Int): Instruction()
data class Jump(val value: Int): Instruction()