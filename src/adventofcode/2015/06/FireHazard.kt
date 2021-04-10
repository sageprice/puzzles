package adventofcode.`2015`.`06`

import java.io.File

fun main() {
  val lightInstrs = File("src/adventofcode/2015/06/input.txt").readLines().map { parseInstruction(it) }

  // Part 1
  val grid = Array(1000) { BooleanArray(1000) { false } }
  for (instr in lightInstrs) {
    for (x in instr.xs) for (y in instr.ys) when (instr.instr) {
      Instr.TOGGLE -> grid[x][y] = !grid[x][y]
      Instr.ON -> grid[x][y] = true
      Instr.OFF -> grid[x][y] = false
    }
  }
  println(grid.map { it.count { b -> b } }.sum())

  // Part 2
  val powGrid = Array(1000) { IntArray(1000) { 0 } }
  for (instr in lightInstrs) {
    for (x in instr.xs) for (y in instr.ys) when (instr.instr) {
      Instr.TOGGLE -> powGrid[x][y] += 2
      Instr.ON -> powGrid[x][y]++
      Instr.OFF -> if (powGrid[x][y] > 0) powGrid[x][y]--
    }
  }
  println(powGrid.map { it.sum() }.sum())

}

private fun parseInstruction(str: String): LightInstr {
  val chunks = str.split(" ")
  val start = chunks[chunks.size - 3].split(",").map { it.toInt() }
  val end = chunks[chunks.size - 1].split(",").map { it.toInt() }
  return if (chunks.size == 4) {
    LightInstr(Instr.TOGGLE, start[0]..end[0], start[1]..end[1])
  } else {
    if (chunks[1] == "on") {
      LightInstr(Instr.ON, start[0]..end[0], start[1]..end[1])
    } else {
      LightInstr(Instr.OFF, start[0]..end[0], start[1]..end[1])
    }
  }
}

private enum class Instr {
  ON,
  OFF,
  TOGGLE
}

private data class LightInstr(val instr: Instr, val xs: IntRange, val ys: IntRange)