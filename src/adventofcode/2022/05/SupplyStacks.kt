package adventofcode.`2022`.`05`

import java.io.File
import java.util.LinkedList

fun main() {
  val input = File("src/adventofcode/2022/05/input.txt").readText()
  val (initConfig, commands) = input.split("\r\n\r\n")
  val configLines = initConfig.split("\r\n").reversed()

  // Part 1
  var stacks = parseStacks(configLines)
  val match = Regex("[a-z ]*(\\d+)[a-z ]*(\\d+)[a-z ]*(\\d+)")
  for (command in commands.split("\r\n")) {
    val (count, start, end) = match.find(command)?.destructured ?: error("Failed to parse: $command")
    repeat(count.toInt()) {
      stacks[end.toInt()-1].add(stacks[start.toInt()-1].removeLast())
    }
  }
  println(stacks.joinToString("") { it.last().toString() })

  // Part 2
  stacks = parseStacks(configLines) // reset
  for (command in commands.split("\r\n")) {
    val (count, start, end) = match.find(command)?.destructured ?: error("Failed to parse: $command")
    // There are smarter ways of doing this but IDC. Use a second list to flip the order.
    val tmp = LinkedList<Char>()
    repeat(count.toInt()) {
      tmp.add(stacks[start.toInt()-1].removeLast())
    }
    stacks[end.toInt()-1].addAll(tmp.reversed())
  }
  println(stacks.joinToString("") { it.last().toString() })
}

private fun parseStacks(configLines: List<String>): List<ArrayDeque<Char>> {
  val buckets = configLines.first().count { it != ' ' }
  val stacks = mutableListOf<ArrayDeque<Char>>()
  for (b in 1..buckets) stacks.add(ArrayDeque())
  for (i in 1 until configLines.size) { // Skip row of bucket indices
    val line = configLines[i]
    for (b in 0 until buckets) {
      if (line[4 * b + 1] != ' ') {
        stacks[b].add(line[4 * b + 1])
      }
    }
  }
  return stacks
}
