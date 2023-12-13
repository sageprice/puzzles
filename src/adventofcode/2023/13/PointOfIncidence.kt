package adventofcode.`2023`.`13`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2023/13/input.txt").readText().split("\r\n\r\n")

  var sum = 0L
  for (block in input) {
    val horizontal = getHorizontalLineOfReflection(block)
    sum += if (horizontal != null) {
      100*horizontal
    } else {
      val vertical = getVerticalLineOfReflection(block) ?: error("No line of reflection found! $block")
      vertical
    }
  }
  println(sum)

  sum = 0L
  for (block in input) {
    val horizontal = getHorizontalLineOfReflection(block, 1)
    sum += if (horizontal != null) {
      100*horizontal
    } else {
      val vertical = getVerticalLineOfReflection(block, 1) ?: error("No line of reflection found! $block")
      vertical
    }
  }
  println(sum)
}

private fun getHorizontalLineOfReflection(reading: String, smudgeCount: Int = 0): Int? {
  val lines = reading.split("\r\n")
  for (i in 0 until lines.size - 1) {
    if (i >= lines.size / 2) {
      val smudges = (0 until lines.size - i - 1).sumOf { j -> getPairSmudgeCount(lines[i-j], lines[i+1+j]) }
      if (smudges == smudgeCount) return i+1
    } else {
      val smudges = (0..i).sumOf { j -> getPairSmudgeCount(lines[i-j], lines[i+1+j]) }
      if (smudges == smudgeCount) return i+1
    }
  }
  return null
}

private fun getPairSmudgeCount(r1: String, r2: String): Int {
  return r1.indices.count { r1[it] != r2[it] }
}

private fun getVerticalLineOfReflection(reading: String, smudgeCount: Int = 0): Int? {
  val lines = reading.split("\r\n")
  val width = lines.first().length
  for (i in 0 until width - 1) {
    if (i >= width / 2) {
      val smudges = (0 until width - i - 1).sumOf { j ->
        val r1 = lines.map { it[i-j] }.joinToString("")
        val r2 = lines.map { it[i+1+j] }.joinToString("")
        getPairSmudgeCount(r1, r2)
      }
      if (smudges == smudgeCount) return i+1
    } else {
      val smudges = (0..i).sumOf { j ->
        val r1 = lines.map { it[i-j] }.joinToString("")
        val r2 = lines.map { it[i+1+j] }.joinToString("")
        getPairSmudgeCount(r1, r2)
      }
      if (smudges == smudgeCount) return i+1
    }
  }
  return null
}

