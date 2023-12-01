package adventofcode.`2023`.`01`

import java.io.File

fun main() {
  val lines: List<String> =
    File("src/adventofcode/2023/01/input.txt")
      .readLines()

  // Part 1
  println(
    lines
      .map { getDigits(it) }
      .sumOf { getCalibration(it) })

  // Part 2
  println(
    lines
      .map { getDigits(replaceDigitSegments(it)) }
      .sumOf { getCalibration(it) })
}

private fun getCalibration(xs: List<Int>): Int = 10 * xs.first() + xs.last()

private fun getDigits(s: String): List<Int> =
  s.filter { it in '0'..'9' }.map { it.toString().toInt() }

private fun replaceDigitSegments(s: String): String {
  var result = ""
  var i = 0
  while (i < s.length) {
    val substr = s.substring(i)
    // don't iterate past the last char in an int string, since it may be the start of another string
    // I thought this wasn't clear from the problem description...
    when {
      substr.startsWith("zero") -> {
        result += "0"
        i += 3
      }
      substr.startsWith("one") -> {
        result += "1"
        i += 2
      }
      substr.startsWith("two") -> {
        result += "2"
        i += 2
      }
      substr.startsWith("three") -> {
        result += "3"
        i += 4
      }
      substr.startsWith("four") -> {
        result += "4"
        i += 3
      }
      substr.startsWith("five") -> {
        result += "5"
        i += 3
      }
      substr.startsWith("six") -> {
        result += "6"
        i += 2
      }
      substr.startsWith("seven") -> {
        result += "7"
        i += 4
      }
      substr.startsWith("eight") -> {
        result += "8"
        i += 4
      }
      substr.startsWith("nine") -> {
        result += "9"
        i += 3
      }
      else -> {
        result += s[i]
        i++
      }
    }
  }
  return result
}