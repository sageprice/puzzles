package adventofcode.`2022`.`11`

import java.io.File

fun main() {
  val monkeys = File("src/adventofcode/2022/11/input.txt")
    .readText()
    .split("\r\n\r\n")
    .map { it.split("\r\n") }

  val monkeysItems: List<List<Long>> = extractMonkeyItems(monkeys)

  // Assumes all divisors are relatively prime (appears to be the case for my input and example).
  val base = (listOf(3L) + // just in case 3 isn't in the list of bases
      monkeys.map { mc -> mc[3].split(" ").last().toLong() })
        .distinct()
        .reduce { a, b -> a * b }
  println("Working in base $base")
  val configs = monkeys.map { mc -> parseMonkeyConfig(mc) }

  var inspections = monkeyAbout(monkeysItems, configs, 20) { x -> x / 3 }
  val monkeyBusiness = inspections.sorted().takeLast(2).reduce { a, b -> a * b }
  println(monkeyBusiness)

  // Part 2
  inspections = monkeyAbout(monkeysItems, configs, 10_000) { x -> x % base }
  val moreMonkeyBusiness = inspections.sorted().takeLast(2).reduce { a, b -> a * b }
  println(moreMonkeyBusiness)
}

private fun monkeyAbout(
  monkeysItems: List<List<Long>>,
  configs: List<MonkeyConfig>,
  rounds: Int,
  worryManagement: (Long) -> Long): List<Long> {
  val items = monkeysItems.toMutableList().map { it.toMutableList() }
  val inspections = MutableList(items.size) { 0L }
  repeat(rounds) {
    for (i in items.indices) {
      inspections[i] = inspections[i] + items[i].size
      val (op, test, tIdx, fIdx) = configs[i]
      for (item in items[i]) {
        val newItem = worryManagement(op(item))
        items[if (test(newItem)) tIdx else fIdx].add(newItem)
      }
      items[i].clear()
    }
  }
  return inspections
}

private fun extractMonkeyItems(monkeys: List<List<String>>) = monkeys.map {
  val (_, xs) = it[1].split(":")
  xs.replace(" ", "")
    .split(",")
    .map { x -> x.toLong() }
    .toMutableList()
}.toMutableList()

private data class MonkeyConfig(
  val op: (Long) -> Long,
  val test: (Long) -> Boolean,
  val trueIndex: Int,
  val falseIndex: Int)

private fun parseMonkeyConfig(mc: List<String>): MonkeyConfig {
  val testMod = mc[3].split(" ").last().toLong()
  val (operator, modifier) = mc[2].split(" ").takeLast(2)
  val op: (Long) -> Long = { x: Long ->
    val right = if (modifier == "old") x else modifier.toLong()
    when (operator) {
      "+" -> (x + right)
      "*" -> (x * right)
      else -> error("Unsupported operation: ${mc[2]}")
    }
  }
  val test = { x: Long -> x % testMod == 0L }
  val trueIndex = mc[4].split(" ").last().toInt()
  val falseIndex = mc[5].split(" ").last().toInt()
  return MonkeyConfig(op, test, trueIndex, falseIndex)
}
