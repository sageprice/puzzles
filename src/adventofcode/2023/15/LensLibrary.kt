package adventofcode.`2023`.`15`

import java.io.File

fun main() {
  val instructions = File("src/adventofcode/2023/15/input.txt").readText().split(",")

  println(instructions.sumOf { runHash(it) })
  println(computeFocusingPower(runHashMap(instructions)))
}

private fun runHash(instrs: String): Int {
  var hashed = 0
  for (x in instrs) {
    hashed += x.code
    hashed *= 17
    hashed %= 256
  }
  return hashed
}

private fun runHashMap(instrs: List<String>): List<List<Pair<String, Int>>> {
  val boxes = Array(256) { listOf<Pair<String, Int>>() }
  for (instr in instrs) {
    if (instr.endsWith("-")) {
      val lens = instr.substring(0, instr.length - 1)
      val box = runHash(lens)
      boxes[box] = boxes[box].filter { it.first != lens }
    } else {
      val (lens, focalLength) = instr.split("=")
      val box = runHash(lens)
      if (boxes[box].any { p -> p.first == lens}) {
        boxes[box] = boxes[box].map { (l: String, c: Int) ->
          if (l == lens) Pair(lens, focalLength.toInt())
          else Pair(l, c)
        }
      } else {
        boxes[box] = boxes[box] + Pair(lens, focalLength.toInt())
      }
    }
  }
  return boxes.toList()
}

private fun computeFocusingPower(boxes: List<List<Pair<String, Int>>>): Long {
  var power = 0L
  boxes.forEachIndexed { idx, box ->
    box.forEachIndexed { lensIdx, (_, lens) ->
      power += (idx+1) * (lensIdx+1) * lens
    }
  }
  return power
}
