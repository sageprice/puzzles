package adventofcode.`2020`.`16`

import java.io.File

fun main() {
  val lines: List<String> =
      File("src/adventofcode/2020/16/input.txt")
          .readLines()

  // Parse input
  //  1. Read initial restrictions
  val restrictions = mutableMapOf<String, List<Pair<Int, Int>>>()
  var i = 0
  while (i < lines.size) {
    if (lines[i].isEmpty()) { i += 2; break }
    val nameAndRange = lines[i].split(": ")
    val ranges = nameAndRange[1]
        .split(" or ")
        .map { it.split("-") }
        .map { Pair(it.first().toInt(), it.last().toInt()) }
    restrictions[nameAndRange[0]] = ranges
    i++
  }
  //  2. Get my ticket information
  val myTicket = lines[i].split(",").map { it.toInt() }
  i += 3
  //  3. Retrieve other tickets
  val otherTickets = mutableListOf<List<Int>>()
  while (i < lines.size) {
    otherTickets.add(lines[i].split(",").map { it.toInt() })
    i++
  }

  // Part 1
  val invalidFieldsSum =
      otherTickets
          .map { getInvalidTicketFields(it, restrictions = restrictions.values.flatten()) }
          .map { it.sum() }
          .sum()
  println("Part 1: $invalidFieldsSum")

  // Part 2
  val validTickets =
      otherTickets.filter { getInvalidTicketFields(it, restrictions.values.flatten()).isEmpty() }
  // Determine which fields each column can represent
  val fieldPossibilities = getFieldPossibilities(validTickets, restrictions)
  // From possible mappings, calculate the unique mapping of field to column that includes all fields
  val finalMappings = calculateFinalMappings(fieldPossibilities)
  val departureFieldsProduct: Long = departureFieldsProduct(finalMappings, myTicket)
  println("Part 2: $departureFieldsProduct")
}

private fun departureFieldsProduct(finalMappings: MutableMap<String, Int>, ticket: List<Int>) =
    finalMappings
        .filterKeys { it.startsWith("departure") }
        .map { (_, i) -> ticket[i].toLong() }
        .reduce { acc, l -> acc * l }

private fun calculateFinalMappings(
    fieldPossibilities: MutableMap<Int, MutableList<String>>
): MutableMap<String, Int> {
  val finalMappings = mutableMapOf<String, Int>()
  val desiredMappingsCount = fieldPossibilities.size
  while (finalMappings.size != desiredMappingsCount) {
    val fieldsToClear = mutableListOf<String>()
    for ((k, v) in fieldPossibilities) {
      if (v.size == 1) {
        finalMappings[v[0]] = k
        fieldsToClear.add(v[0])
      }
    }
    fieldsToClear.forEach { ftc ->
      fieldPossibilities.forEach { (_, v) ->
        if (v.contains(ftc)) v.remove(ftc)
      }
    }
  }
  return finalMappings
}

private fun getFieldPossibilities(
    validTickets: List<List<Int>>,
    restrictions: MutableMap<String, List<Pair<Int, Int>>>
): MutableMap<Int, MutableList<String>> {
  val enumeratedFields = mutableMapOf<Int, MutableList<String>>()
  for (i in validTickets[0].indices) {
    enumeratedFields[i] = mutableListOf()
    val fieldValues = validTickets.map { it[i] }
    for (f in restrictions.keys) {
      val restriction = restrictions[f]!!
      if (fieldValues.all { v ->
            val r1 = restriction[0]
            val r2 = restriction[1]
            v in r1.first..r1.second || v in r2.first..r2.second
          }) {
        enumeratedFields[i]!!.add(f)
      }
    }
  }
  return enumeratedFields
}

private fun getInvalidTicketFields(
    ticket: List<Int>,
    restrictions: List<Pair<Int, Int>>
): List<Int> {
  return ticket.filter { v -> restrictions.none { (a, b) -> v in a..b } }
}