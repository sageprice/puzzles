package adventofcode.`2025`.`06`

import java.io.File

/** https://adventofcode.com/2025/day/6 */
fun main() {
  val input = File("src/adventofcode/2025/06/input.txt").readLines()

  println(doTheEasyMath(input).sum())
  println(doTheCephalopodMath(input).sum())
}

private fun doTheEasyMath(input: List<String>): List<Long> {
  val regex = "\\d+".toRegex()
  val nums = input.take(input.size - 1).map {
    regex.findAll(it).map { mr -> mr.value.toLong() }.toList()
  }
  val ops = input.last().filter { it != ' ' }
  return ops.mapIndexed { idx, op ->
    nums.map { it[idx] }.reduce { a, b ->
      if (op == '+') {
        a+b
      } else {
        a*b
      }
    }
  }
}

private fun doTheCephalopodMath(input: List<String>): List<Long> {
  val results = mutableListOf<Long>()
  var op: String? = ""
  var amount = 0L
  val longestRow = input.maxOf { it.length }
  // assume space padding
  for (i in 0..longestRow) {
    val column = input.map { it.getOrNull(i)?.toString() ?: " " }
    // All empty => new problem. Reset
    if (column.all { it.isBlank() || it.isEmpty() }) {
      results.add(amount)
      amount = 0
      continue
    }
    if (column.last().isNotBlank()) {
      op = column.last()
    }
    val digits = column.mapNotNull { it.toIntOrNull() }
    val num = digits.joinToString("").toLong()
    if (amount == 0L) {
      amount = num
    } else if (op == "+") {
      amount += num
    } else if (op == "*") {
      amount *= num
    } else {
      throw IllegalStateException("Op cannot be [$op] at index [$i]. Column $column")
    }
  }
  results.add(amount)
  return results
}
