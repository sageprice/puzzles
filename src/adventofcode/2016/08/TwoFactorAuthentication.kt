package adventofcode.`2016`.`08`

import java.io.File

fun main() {
  val instructions = File("src/adventofcode/2016/08/input.txt").readLines().map { parseInstruction(it) }
  val output = runInstructions(instructions)
  println(output.sumOf { row -> row.count { it } })
  printArray(output)
}

private fun runInstructions(instructions: List<Instruction>): Array<BooleanArray> {
  var arr = Array(6) {
    BooleanArray(50)
  }
  instructions.forEach { instr ->
    arr = apply(instr, arr)
  }
  return arr
}

private fun printArray(arr: Array<BooleanArray>) {
  for (r in arr.indices) {
    for (c in arr[r]) print(if (c) "#" else " ")
    println()
  }
}

private fun apply(instr: Instruction, arr: Array<BooleanArray>): Array<BooleanArray> {
  arr.map { it.toList().toBooleanArray() }.toTypedArray()
  val nextArr = arr.map { it.toList().toBooleanArray() }.toTypedArray()
  when (instr) {
    is Rectangle -> {
      for (r in 0 until instr.y) for (c in 0 until instr.x) {
        nextArr[r][c] = true
      }
    }
    is RotateRow -> {
      val rowLen = nextArr.first().size
      for (c in nextArr[0].indices) {
        nextArr[instr.index][(c + instr.amount) % rowLen] = arr[instr.index][c]
      }
    }
    is RotateColumn -> {
      val colLen = nextArr.size
      for (r in nextArr.indices) {
        nextArr[(r + instr.amount) % colLen][instr.index] = arr[r][instr.index]
      }
    }
  }
  return nextArr
}

private fun parseInstruction(str: String): Instruction {
  val chunks = str.split(" ")
  if (chunks.size == 2) {
    val amts = chunks.last().split("x")
    return Rectangle(amts.first().toInt(), amts.last().toInt())
  }
  if (chunks[1] == "row") {
    val index = chunks[2].split("=").last().toInt()
    val amount = chunks.last().toInt()
    return RotateRow(index, amount)
  } else {
    val index = chunks[2].split("=").last().toInt()
    val amount = chunks.last().toInt()
    return RotateColumn(index, amount)
  }
}

private sealed class Instruction
private data class Rectangle(val x: Int, val y: Int): Instruction()
private data class RotateRow(val index: Int, val amount: Int): Instruction()
private data class RotateColumn(val index: Int, val amount: Int): Instruction()
