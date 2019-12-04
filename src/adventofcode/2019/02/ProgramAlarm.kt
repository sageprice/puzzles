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
  println(evalSpec(programSpec, 12, 2))

  // Part 2
  val answer = 19690720
  for (noun in 1..100) {
    for (verb in 1..100) {
      if (evalSpec(programSpec, noun, verb) == answer) {
        println(noun * 100 + verb)
        return
      }
    }
  }
}

fun evalSpec(spec: List<Int>, noun: Int, verb: Int): Int {
  val mutSpec: MutableList<Int> = spec.toMutableList()
  mutSpec[1] = noun
  mutSpec[2] = verb
  var programIndex: Int = 0
  while (mutSpec.get(programIndex) != 99) {
    val first = mutSpec[mutSpec[programIndex + 1]]
    val second = mutSpec[mutSpec[programIndex + 2]]
    val target = mutSpec[programIndex + 3]
    when (mutSpec[programIndex]) {
      1 -> mutSpec[target] = first + second
      2 -> mutSpec[target] = first * second
      else -> return -1
    }
    programIndex += 4
  }
  return mutSpec.get(0)
}