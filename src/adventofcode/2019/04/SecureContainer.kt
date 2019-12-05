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
  var satisfactoryPasswords = 0
  for (i in low..high) {
    var copy = i
    var previousDigit = 10
    var isAscendingDigits = true
    var hasSameAdjacentDigits = false
    while (copy > 0) {
      val lastDigit = copy % 10
      isAscendingDigits = lastDigit <= previousDigit
      if (!isAscendingDigits) {
        break
      }
      if (lastDigit == previousDigit) {
        hasSameAdjacentDigits = true
      }
      copy /= 10
      previousDigit = lastDigit
    }
    if (isAscendingDigits && hasSameAdjacentDigits) {
      satisfactoryPasswords++
    }
  }
  println("Part 1: $satisfactoryPasswords")
}