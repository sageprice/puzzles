package adventofcode.`2019`.`04`

import java.io.File

/**
 * Solution for [Advent of Code day 4](https://adventofcode.com/2019/day/4).
 */
fun main() {
  // Input parameters
  val low = 367479
  val high = 893698

  // Part 1
  val possiblePasswords = 
      (low..high).filter { hasNonDecreasingDigits(it) && hasRepeatedDigit(it) }
  println("Part 1: ${possiblePasswords.size}")

  // Part 2
  val constrainedPws: List<Int> = 
      (low..high)
          .filter { hasNonDecreasingDigits(it) && hasDigitRepeatLengthTwo(it) }
  println("Part 2: ${constrainedPws.size}")
}

/** 
 * Returns true IFF the given integer has some adjacent digits which are equal. 
 */
fun hasRepeatedDigit(i: Int): Boolean {
  var copy = i
  var previousDigit = 10
  while (copy > 0) {
    val lastDigit = copy % 10
    if (lastDigit == previousDigit) {
      return true
    }
    copy /= 10
    previousDigit = lastDigit
  }
  return false
}

/** 
 * Returns true IFF the given integer is a sequence of non-decreasing digits.
 * E.g. inputs of 123456, 11578, 8999999999 all return true. 10 returns false.
 */
fun hasNonDecreasingDigits(i: Int): Boolean {
  var copy = i
  var previousDigit = 10
  while (copy > 0) {
    val lastDigit = copy % 10
    if (lastDigit > previousDigit) return false
    copy /= 10
    previousDigit = lastDigit
  }
  return true
}

fun hasDigitRepeatLengthTwo(i: Int): Boolean {
  var copy = i
  while (copy > 0) {
    var acc = 0
    val lastDigit = copy % 10
    while (copy % 10 == lastDigit) {
      copy /= 10
      acc = acc * 10 + lastDigit
    }
    if (acc > 10 && 100 > acc) return true
  }
  return false
}