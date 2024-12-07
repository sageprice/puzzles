package adventofcode.`2024`.`07`

import java.io.File

/** https://adventofcode.com/2024/day/7 */
fun main() {
  val eqs = File("src/adventofcode/2024/07/input.txt").readLines().map { l ->
    val (res, ins) = l.split(": ")
    Equation(res.toLong(), ins.split(" ").map { it.toLong() })
  }
  // Part 1
  val firstOps = listOf<(Long, Long) -> Long>(
    { a, b -> a + b},
    { a, b -> a * b},
  )
  println(eqs.filter { (res, ins) -> res in getPossibleOutputs(ins, firstOps) }.sumOf { it.res })
  // Part 2
  val secondOps = firstOps + { a, b -> (a.toString() + b.toString()).toLong() }
  println(eqs.filter { (res, ins) -> res in getPossibleOutputs(ins, secondOps) }.sumOf { it.res })
}

private fun getPossibleOutputs(inputs: List<Long>, fns: List<(Long, Long) -> Long>): Set<Long> {
  if (inputs.isEmpty()) throw IllegalStateException("Cannot have empty input list")
  if (inputs.size == 1) return inputs.toSet()
  val end = inputs.last()
  val possible = getPossibleOutputs(inputs.subList(0, inputs.size - 1), fns)
  return fns.flatMap { fn ->
    possible.map { fn(it, end) }
  }.toSet()
}

private data class Equation(val res: Long, val inputs: List<Long>)
