package adventofcode.`2021`.`25`

import java.io.File

fun main() {
  var floor: List<List<String>> = File("src/adventofcode/2021/25/input.txt")
    .readLines()
    .map { it.split("").filter { s -> s.isNotBlank() } }

  // Part 1
  var next: List<List<String>> = floor.map { it.toList() }
  var c = 0
  do {
    c++
    floor = next
    next = stepSouth(stepEast(next))
//    println("After $c steps we have")
//    printFloor(next)
  } while (!sameElements(floor, next))
  println(c)
}

private fun stepSouth(floor: List<List<String>>): List<List<String>> {
  val nextStep = floor.map { it.toMutableList() }
  for (row in floor.indices) {
    val prevRow = if (row == 0) floor.size-1 else row-1
    for (column in floor[row].indices) {
      if (floor[prevRow][column] == "v" && floor[row][column] == ".") {
        nextStep[row][column] = "v"
        nextStep[prevRow][column] = "."
      }
    }
  }
  return nextStep
}

private fun stepEast(floor: List<List<String>>): List<List<String>> {
  val nextStep = floor.map { it.toMutableList() }
  for (column in floor.first().indices) {
    val prevCol = if (column == 0) floor.first().size-1 else column-1
    for (row in floor.indices) {
      if (floor[row][prevCol] == ">" && floor[row][column] == ".") {
        nextStep[row][column] = ">"
        nextStep[row][prevCol] = "."
      }
    }
  }
  return nextStep
}

private fun <T> sameElements(l1: List<List<T>>, l2: List<List<T>>): Boolean {
  if (l1.size != l2.size) return false
  for (i in l1.indices) {
    if (l1[i].size != l2[i].size) return false
    for (j in l1[i].indices) {
      if (l1[i][j] != l2[i][j]) return false
    }
  }
  return true
}

private fun printFloor(l: List<List<String>>) {
  l.forEach { println(it.joinToString("")) }
}