package adventofcode.`2024`.`17`

import java.io.File
import kotlin.math.pow

/**
 * https://adventofcode.com/2024/day/17
 *
 * Part 2 boils down to inspecting the input, and realizing that each pass through it is a loop resulting in a division
 * of A by 8.
 *
 * This means that we can effectively work backwards through the outputs. Once we find an input that generates the last
 * number in a program, we multiply that number by 8, and test each possible value for the least significant digit. And
 * since we're dividing by 8 each time, we only really need to test the digits 0 through 7 in each iteration.
 *
 * This yields a recursive approach that rapidly converges on the answer, and is guaranteed to be minimal if we work
 * from 0 up to 7 in each iteration. See `reconstruct` for the recursive exploration.
 */
fun main() {
  val input = File("src/adventofcode/2024/17/input.txt").readLines()
  val regA = input[0].split(": ").last().toLong()
  val regB = input[1].split(": ").last().toLong()
  val regC = input[2].split(": ").last().toLong()
  val prog = input[4].split(": ").last().split(",").map { it.toInt() }

  // Part 1
  println(run(regA, regB, regC, prog).joinToString(","))

  // Part 2
  println(reconstruct(emptyList(), regB, regC, prog))
}

private fun reconstruct(a: List<Long>, b: Long, c: Long, program: List<Int>, depth: Int = 0): Long? {
  if (a.size == program.size) {
    return a.reduce { acc, l -> 8*acc + l }
  }
  val start = if (a.isEmpty()) 0 else a.reduce { acc, l -> 8*acc + l }
  val pTail = program.subList(program.size - a.size - 1, program.size).map { it.toLong() }
  for (i in 0L..7) {
    val output = run(start * 8 + i, b, c, program)
    val tail = output.subList(output.size - a.size - 1, output.size)
    if (pTail == tail) {
      // It's possible we find a starting point that doesn't reach our target,
      // so we have to be able to backtrack and explore other values.
      val result = reconstruct(a + i, b, c, program, depth + 1)
      if (result != null) return result
    }
  }
  return null
}

private fun run(a: Long, b: Long, c: Long, program: List<Int>, breakOnJump: Boolean = false): List<Long> {
  val regs = longArrayOf(a, b, c)
  var pointer = 0
  val outputs = mutableListOf<Long>()
  while (pointer in program.indices) {
    val operand = program[pointer + 1]
    when(program[pointer]) {
      0 -> { // adv
        regs[0] = regs[0] / 2.0.pow(comboValue(operand.toLong(), regs).toInt()).toInt()
        pointer += 2
      }
      1 -> { // bxl
        regs[1] = regs[1].xor(operand.toLong())
        pointer += 2
      }
      2 -> { // bst
        regs[1] = comboValue(operand.toLong(), regs) % 8
        pointer += 2
      }
      3 -> { // jnz
        if (regs[0] == 0L) pointer += 2 else pointer = operand
        if (breakOnJump) return outputs
      }
      4 -> { // bxc
        regs[1] = regs[1].xor(regs[2])
        pointer += 2
      }
      5 -> { // out
        outputs.add(comboValue(operand.toLong(), regs) % 8)
        pointer += 2
      }
      6 -> { // bdv
        regs[1] = regs[0] / 2.0.pow(comboValue(operand.toLong(), regs).toInt()).toInt()
        pointer += 2
      }
      7 -> { // cdv
        regs[2] = regs[0] / 2.0.pow(comboValue(operand.toLong(), regs).toInt()).toInt()
        pointer += 2
      }
    }
  }
  return outputs
}

private fun comboValue(x: Long, regs: LongArray): Long {
  return if (x < 4) x else when (x) {
    4L -> regs[0]
    5L -> regs[1]
    6L -> regs[2]
    else -> throw IllegalArgumentException("Unknown combo op `$x`")
  }
}
