package adventofcode.`2020`.`19`

import java.io.File

fun main() {
  val input: List<String> =
    File("src/adventofcode/2020/19/input.txt")
      .readLines()

  val (rules, messages) = parse(input)
  val ruleEncodings = mutableMapOf<Rule, Int>()
  for ((id, rule) in rules) {
    rule.forEach { ruleEncodings[it] = id }
  }

  // Part 1
  val possible = buildStrings((rules[0]!![0] as Transform).rs, rules).toSet()
  println("Part 1: ${messages.count { possible.contains(it) }}")

  // Part 2
  val str42s = rules[42]!!.map { buildStrings((it as Transform).rs, rules) }.flatten()
  val str31s = rules[31]!!.map { buildStrings((it as Transform).rs, rules) }.flatten()

  val transformed = messages.map { replaceTail(replaceHead(it, str42s), str31s) }
  val meetRules =
    transformed
      .filterNot { it.contains("a") || it.contains("b") }
      .count { word ->
        val hCount = word.count { it == 'H' }
        val tCount = word.count { it == 'T' }
        tCount in 1 until hCount
      }
  println("Part 2: $meetRules")
}

private fun replaceHead(msg: String, tails: List<String>): String {
  var headRepeatCount = 0
  var outMsg = msg
  while (true) {
    var anyMatch = false
    tails.forEach {
      if (outMsg.startsWith(it)) {
        outMsg = outMsg.removePrefix(it)
        headRepeatCount++
        anyMatch = true
      }
    }
    if (!anyMatch) break
  }
  return (1..headRepeatCount).joinToString("") { "H" } + outMsg
}

private fun replaceTail(msg: String, tails: List<String>): String {
  var tailRepeatCount = 0
  var outMsg = msg
  while (true) {
    var anyMatch = false
    tails.forEach {
      if (outMsg.endsWith(it)) {
        outMsg = outMsg.removeSuffix(it)
        tailRepeatCount++
        anyMatch = true
      }
    }
    if (!anyMatch) break
  }
  return outMsg + (1..tailRepeatCount).joinToString("") { "T" }
}

private fun buildStrings(xs: List<Int>, rules: Map<Int, List<Rule>>): List<String> {
  val results = xs.map { x ->
    rules[x]!!.map { r ->
      when (r) {
        is Base -> listOf(r.c.toString())
        is Transform -> {
          buildStrings(r.rs, rules)
        }
      }
    }.flatten()
  }
  return construct(results)
}

private fun construct(ins: List<List<String>>): List<String> {
  if (ins.isEmpty()) return listOf("")
  val rest = construct(ins.subList(1, ins.size))
  return ins[0].map { x -> rest.map { x + it } }.flatten()
}

private fun parse(lines: List<String>): Pair<Map<Int, List<Rule>>, List<String>> {
  val rules = mutableMapOf<Int, List<Rule>>()
  val messages = mutableListOf<String>()
  var readingMessages = false
  for (line in lines) {
    if (line.isBlank()) {
      readingMessages = true
      continue
    }
    if (readingMessages) {
      messages.add(line)
    } else {
      val (ruleNum, conditions) = line.split(": ")
      if (conditions.startsWith("\"")) {
        val baseChar = conditions.removeSurrounding("\"")[0]
        rules[ruleNum.toInt()] = listOf(Base(baseChar))
      } else {
        val transforms =
          conditions
            .split(" | ")
            .map { it.split(" ").map { num -> num.toInt() } }
            .map { Transform(it) }
        rules[ruleNum.toInt()] = transforms
      }
    }
  }
  return Pair(rules, messages)
}

private sealed class Rule
private data class Base(val c: Char): Rule()
private data class Transform(val rs: List<Int>): Rule()