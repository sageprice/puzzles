package adventofcode.`2015`.`05`

import java.io.File

fun main() {
  val strings = File("src/adventofcode/2015/05/input.txt").readLines()

  println("Part 1: " + strings.count { isNice(it) })
  println("Part 2: " + strings.count { isNicer(it) })
}

private fun isNice(str: String): Boolean {
  var lastChar: Char? = null
  var hasDoubledLetter = false
  var vowelCount = 0
  for (c in str) {
    if (c in "aeiou") vowelCount++
    if (lastChar != null) {
      val bigram = "$lastChar$c"
      if (bigram in "ab|cd|pq|xy") return false
      if (lastChar == c) hasDoubledLetter = true
    }
    lastChar = c
  }
  return vowelCount >= 3 && hasDoubledLetter
}

private fun isNicer(str: String): Boolean {
  return hasRepeatedBigram(str) && hasPalindromicTrigram(str)
}

private fun hasRepeatedBigram(str:String): Boolean {
  for (i in 0 until str.length - 3) {
    val substr = str.substring(i,i+2)
    if (substr in str.substring(i+2)) return true
  }
  return false
}

private fun hasPalindromicTrigram(str: String) : Boolean {
  for (i in 0 until str.length - 2) {
    if (str[i] == str[i+2]) return true
  }
  return false
}