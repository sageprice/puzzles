package adventofcode.`2015`.`08`

import java.io.File

fun main() {
  val lines = File("src/adventofcode/2015/08/input.txt").readLines()
  println(lines.sumBy { it.length} - lines.map { simplifyLine(it) }.sumBy { it.length })
  println(lines.map { complexifyLine(it) }.sumBy { it.length } - lines.sumBy { it.length})
}

fun simplifyLine(line: String): String {
  var str = line.substring(1, line.length - 1)
  var i = 0
  while (i < str.length - 1) {
    when {
      str.subSequence(i, i+2) == "\\\\" -> {
        str = str.substring(0, i) + str.substring(i+1)
      }
      str.subSequence(i, i+2) == "\\\"" -> {
        str = str.substring(0, i) + str.substring(i+1)
      }
      str.subSequence(i, i+2) == "\\x" -> {
        str = str.substring(0, i) + "x" + str.substring(i+4)
      }
    }
    i++
  }
  return str
}

fun complexifyLine(line: String): String {
  return "\"" + line.replace("\\", "\\\\").replace("\"", "\\\"") + "\""

}