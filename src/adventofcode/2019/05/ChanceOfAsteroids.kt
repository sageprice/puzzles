package adventofcode.`2019`.`05`

import adventofcode.`2019`.intcode.evalSpec
import java.io.File

/**
 * Solution for [Advent of Code day 5](https://adventofcode.com/2019/day/5).
 */
fun main() {
  // Read input file
  val programSpec: List<Long> =
      File("src/adventofcode/2019/05/input.txt")
      // File("test_input.txt")
         .readLines()[0].split(",").map { it.toLong() }

  // Part 1 -- provide input 1
  // Part 2 -- provide input 5
  println("${evalSpec(programSpec)}")
}