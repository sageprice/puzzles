package adventofcode.`2023`.`18`

import java.io.File
import kotlin.math.abs

fun main() {
  val instrs = File("src/adventofcode/2023/18/input.txt").readLines().map { parseInstruction(it) }

  println(shoelace(getSmallLagoon(instrs)))

  println(shoelace(getBigLagoon(instrs)))
}

private fun shoelace(points: List<Pair<Long, Long>>): Long {
  var a = 0L
  var trenchLen = 0L
  for (i in 0 until points.size - 1) {
    a += points[i].first * points[i+1].second - points[i].second * points[i+1].first
    trenchLen += abs(points[i].first - points[i+1].first) + abs(points[i].second - points[i+1].second)
  }
  // Absolute value since points may be going clockwise...
  return abs(a) / 2 + trenchLen / 2 + 1
}

private fun getBigLagoon(instrs: List<Instruction>): List<Pair<Long, Long>> {
  val points = mutableListOf(Pair(0L, 0L))
  var r = 0L
  var c = 0L
  for (instr in instrs) {
    when (instr.hexDir) {
      0 -> c += instr.hexDist
      1 -> r += instr.hexDist
      2 -> c -= instr.hexDist
      3 -> r -= instr.hexDist
    }
    points.add(Pair(r, c))
  }
  return points
}

private fun getSmallLagoon(instrs: List<Instruction>): List<Pair<Long, Long>> {
  var r = 0L
  var c = 0L
  val points = mutableListOf(Pair(r, c))
  for (instr in instrs) {
    when (instr.dir) {
      "U" -> r -= instr.steps
      "D" -> r += instr.steps
      "L" -> c -= instr.steps
      "R" -> c += instr.steps
    }
    points.add(Pair(r, c))
  }
  return points
}

private fun parseInstruction(input: String): Instruction {
  val (dir, steps, color) = input.split(" ")
  val (hexDist, hexDir) = color.substring(2, color.length - 1).chunked(5)
  return Instruction(dir, steps.toInt(), hexDist.toLong(radix = 16), hexDir.toInt())
}

private data class Instruction(
  val dir: String,
  val steps: Int,
  val hexDist: Long,
  val hexDir: Int)
