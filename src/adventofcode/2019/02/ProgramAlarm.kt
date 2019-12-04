package adventofcode.`2019`.`02`

import java.io.File

/**
 * Solution for [Advent of Code day 2](https://adventofcode.com/2019/day/2).
 */
fun main() {
  // Read input file
  val programSpec: List<Int> =
      File("input.txt")
      // File("test_input.txt")
         .readLines().get(0).split(",").map { it.toInt() }
  println(programSpec)

  // Part 1
  val mutSpec: MutableList<Int> = programSpec.toMutableList()
  mutSpec[1] = 12
  mutSpec[2] = 2
  var programIndex: Int = 0
  while (mutSpec.get(programIndex) != 99) {
    println("----------------------------------------------------------")
    println(mutSpec.slice(programIndex until programIndex + 4))
    val first = mutSpec[mutSpec[programIndex + 1]]
    val second = mutSpec[mutSpec[programIndex + 2]]
    val target = mutSpec[programIndex + 3]
    when (mutSpec[programIndex]) {
      1 -> {
        println("Adding ${first} and ${second} into location ${target}")
        mutSpec[target] = first + second
        println(mutSpec[target])
        println(mutSpec.slice(0 until target + 1))
      }
      2 -> {
        println("Multiplying ${first} and ${second} into location ${target}")
        mutSpec[target] = first * second
        println(mutSpec.get(target))
        println(mutSpec.slice(0 until target + 1))
      }
      else -> {
        throw Exception("PANIC")
      }
    }
    programIndex += 4
    println(mutSpec)
  }
  println(mutSpec.get(0))
}