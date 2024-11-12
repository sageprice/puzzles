package adventofcode.`2016`.`09`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2016/09/input.txt").readText()
  println(getExpandedLength(input))
  println(getExpandedLengthV2(input))
}

private fun getExpandedLength(str: String): Long {
  var len = 0L
  var index = 0
  while (index < str.length) {
    if (str[index] == '(') {
      val endIndex = str.indexOf(')', startIndex = index)
      val (repLen, reps) = str.substring(index+1, endIndex).split("x").map { it.toInt() }
      len += repLen * reps
      index = endIndex + repLen + 1
    } else {
      len += 1
      index += 1
    }
  }
  return len
}

private fun getExpandedLengthV2(str: String): Long {
  var len = 0L
  var index = 0
  while (index < str.length) {
    if (str[index] == '(') {
      val endIndex = str.indexOf(')', startIndex = index)
      val (repLen, reps) = str.substring(index+1, endIndex).split("x").map { it.toInt() }
      len += reps * getExpandedLengthV2(str.substring(endIndex+1, endIndex+1+repLen))
      index = endIndex + repLen + 1
    } else {
      len += 1
      index += 1
    }
  }
  return len
}
