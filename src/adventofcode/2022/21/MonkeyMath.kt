package adventofcode.`2022`.`21`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/21/input.txt")
    .readLines()
    .map { parseYell(it) }

  // Part 1
  val waitingMonkeys = mutableMapOf<String, Yell>()
  input.forEach { (k, v) -> waitingMonkeys[k] = v }
  val yellResults = propagateMonkeyYells(input)
  println(yellResults["root"])

  // Part 2s
  println(calculateYellFrom("humn", "root", input, yellResults))
}

private fun calculateYellFrom(
  firstYell: String,
  lastYell: String,
  monkeyOps: List<Pair<String, Yell>>,
  yellResults: Map<String, Long>
): Long {
  val yells = mutableMapOf<String, Arithmetic>()
  for (l in monkeyOps) if (l.second is Arithmetic) yells[l.first] = l.second as Arithmetic
  val (_, r1, r2) = monkeyOps.first { it.first == lastYell }.second as Arithmetic
  var value = 0L
  var invertMonkey = lastYell
  if (firstYell in getDependents(r1, yells)) {
    value = yellResults[r2]!!
    invertMonkey = r1
  } else if (firstYell in getDependents(r2, yells)) {
    value = yellResults[r1]!!
    invertMonkey = r2
  }
  while (invertMonkey != firstYell) {
    val (op, m1, m2) = monkeyOps.first { it.first == invertMonkey }.second as Arithmetic
    if (firstYell in getDependents(m1, yells) || firstYell == m1) {
      value = when (op) {
        "*" -> value / yellResults[m2]!!
        "/" -> value * yellResults[m2]!!
        "+" -> value - yellResults[m2]!!
        "-" -> value + yellResults[m2]!!
        else -> error("Unknown operation $op, $m1, $m2 for monkey $invertMonkey")
      }
      invertMonkey = m1
    } else if (firstYell in getDependents(m2, yells) || firstYell == m2) {
      value = when (op) {
        "*" -> value / yellResults[m1]!!
        "/" -> yellResults[m1]!! / value
        "+" -> value - yellResults[m1]!!
        "-" -> yellResults[m1]!! - value
        else -> error("Unknown operation $op, $m1, $m2 for monkey $invertMonkey")
      }
      invertMonkey = m2
    }
  }
  return value
}

private fun getDependents(monkey: String, dependencies: Map<String, Arithmetic>): List<String> {
  return if (monkey in dependencies) {
    val (_, m1, m2) = dependencies[monkey]!!
    getDependents(m1, dependencies) + getDependents(m2, dependencies) + listOf(m1, m2)
  } else emptyList()
}

private fun propagateMonkeyYells(monkeyOps: List<Pair<String, Yell>>): Map<String, Long> {
  val waitingMonkeys = mutableMapOf<String, Yell>()
  monkeyOps.forEach { (k, v) -> waitingMonkeys[k] = v }
  val monkeyYell = mutableMapOf<String, Long>()
  while (monkeyYell.size != monkeyOps.size) {
    val doneMonkeys = mutableSetOf<String>()
    for ((m, y) in waitingMonkeys) {
      if (y is Constant) {
        monkeyYell[m] = y.x
        doneMonkeys.add(m)
      } else {
        val (op, m1, m2) = y as Arithmetic
        if (m1 in monkeyYell && m2 in monkeyYell) {
          monkeyYell[m] = when (op) {
            "*" -> monkeyYell[m1]!! * monkeyYell[m2]!!
            "/" -> monkeyYell[m1]!! / monkeyYell[m2]!!
            "+" -> monkeyYell[m1]!! + monkeyYell[m2]!!
            "-" -> monkeyYell[m1]!! - monkeyYell[m2]!!
            else -> error("What is this monkey??? $m: $y")
          }
          doneMonkeys.add(m)
        }
      }
    }
    doneMonkeys.forEach { waitingMonkeys.remove(it) }
  }
  return monkeyYell
}

private sealed class Yell
private data class Constant(val x: Long): Yell()
private data class Arithmetic(val op: String, val m1: String, val m2: String): Yell()

private fun parseYell(s: String): Pair<String, Yell> {
  val (monkey, rest) = s.split(": ")
  // Too lazy to care
  if (rest.length < 5) return Pair(monkey, Constant(rest.toLong()))
  val (a, op, b) = rest.split(" ")
  return Pair(monkey, Arithmetic(op, a, b))
}