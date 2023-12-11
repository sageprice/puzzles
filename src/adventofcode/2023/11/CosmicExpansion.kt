package adventofcode.`2023`.`11`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val input = File("src/adventofcode/2023/11/input.txt").readLines()
  val emptyRows = getEmptyRows(input)
  val emptyCols = getEmptyColumns(input)
  val galaxies = findGalaxies(input)

  var p1 = 0L
  for (i in galaxies.indices) for (j in i+1 until galaxies.size) {
    p1 += getExpandedDistance(galaxies[i], galaxies[j], emptyRows, emptyCols)
  }
  println(p1)

  var p2 = 0L
  for (i in galaxies.indices) for (j in i+1 until galaxies.size) {
    p2 += getExpandedDistance(galaxies[i], galaxies[j], emptyRows, emptyCols, 1_000_000)
  }
  println(p2)
}

private fun findGalaxies(input: List<String>): MutableList<Pair<Int, Int>> {
  val galaxies = mutableListOf<Pair<Int, Int>>()
  for (r in input.indices) for (c in input[r].indices) {
    if (input[r][c] == '#') galaxies.add(Pair(r, c))
  }
  return galaxies
}

private fun getExpandedDistance(
  g1: Pair<Int, Int>, g2: Pair<Int, Int>, expRows: Set<Int>, expCols: Set<Int>, expansionFactor: Long = 2): Long {
  var d = 0L
  var low = min(g1.first, g2.first)
  var high = max(g1.first, g2.first)
  d += (low until high).sumOf { if (it in expRows) expansionFactor else 1L }
  low = min(g1.second, g2.second)
  high = max(g1.second, g2.second)
  d += (low until high).sumOf { if (it in expCols) expansionFactor else 1L }
  return d
}

private fun getEmptyRows(photo: List<String>): Set<Int> {
  return photo.indices.filter { r ->
    photo[r].all { it == '.' }
  }.toSet()
}

private fun getEmptyColumns(photo: List<String>): Set<Int> {
  return photo.first().indices.filter { c ->
    photo.all { it[c] == '.' }
  }.toSet()
}
