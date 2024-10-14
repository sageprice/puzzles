package adventofcode.`2015`.`23`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2015/23/input.txt").readLines()
  println(runProgram(input))
  println(runProgram(input, 1))
}

private fun runProgram(lines: List<String>, aStart: Int = 0): Int {
  var a = aStart
  var b = 0
  var idx = 0
  while (idx < lines.size) {
    val line = lines[idx]
    when (line.substring(0, 3)) {
      "jio" -> {
        val register = line.substring(4, 5)
        val offset = line.substring(7).toInt()
        if ((register == "a" && a == 1) || (register == "b" && b == 1)) {
          idx += offset
        } else idx++
      }
      "jie" -> {
        val register = line.substring(4, 5)
        val offset = line.substring(7).toInt()
        if ((register == "a" && a % 2 == 0) || (register == "b" && b % 2 == 0)) {
          idx += offset
        } else idx++
      }
      "jmp" -> idx += line.substring(4).toInt()
      else -> {
        val register = line.substring(4)
        when (line.substring(0, 3)) {
          "hlf" -> if (register == "a") a /= 2 else b /= 2
          "tpl" -> if (register == "a") a *= 3 else b *= 3
          "inc" -> if (register == "a") a++ else b++
        }
        idx++
      }
    }
  }
  return b
}
