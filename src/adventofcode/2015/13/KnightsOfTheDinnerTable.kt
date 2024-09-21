package adventofcode.`2015`.`13`

import java.io.File

fun main() {
  val relationships = File("src/adventofcode/2015/13/input.txt").readLines().map { parseRelationship(it) }
  val relationGrid = getPairwiseRelations(relationships)
  println(getOptimalSeating(relationGrid))
  println(getOptimalSeating(addSelfToGrid(relationGrid)))
}

private val RELATIONSHIP_PATTERN = Regex("(?<source>[A-Za-z]+) would (?<change>[losegain]+) (?<amt>\\d+) happiness units by sitting next to (?<cause>[A-Za-z]+).")

private fun parseRelationship(line: String): Relationship {
  val groups = RELATIONSHIP_PATTERN.matchEntire(line)?.groupValues!!

  return Relationship(
    impacted = groups[1],
    change = if (groups[2] == "lose") 0-groups[3].toLong() else groups[3].toLong(),
    cause = groups[4])
}

private fun getPairwiseRelations(relations: List<Relationship>): Map<Pair<String, String>, Long> {
  val pairMap = mutableMapOf<Pair<String, String>, Long>()
  relations.forEach { (a, change, b) ->
    pairMap[a to b] = (pairMap[a to b] ?: 0) + change
    pairMap[b to a] = (pairMap[b to a] ?: 0) + change
  }
  return pairMap
}

private fun getOptimalSeating(grid: Map<Pair<String, String>, Long>): Long {
  val diners = grid.keys.map { it.first }.distinct()
  val seated = listOf(diners.minOf { it })
  return getOptimalSeating(grid, seated, diners - seated)
}

private fun getOptimalSeating(
  grid: Map<Pair<String, String>, Long>,
  seated: List<String>,
  unseated: List<String>): Long {
  return if (unseated.isEmpty()) {
    getSeatingHappiness(grid, seated)
  } else {
    unseated.maxOf { who ->
      getOptimalSeating(grid, seated + who, unseated - who)
    }
  }
}

private fun getSeatingHappiness(
  grid: Map<Pair<String, String>, Long>,
  seated: List<String>): Long {
  return seated.indices.sumOf {
    val neighbor = if (it == seated.size - 1) seated[0] else seated[it+1]
    grid[seated[it] to neighbor]!!
  }
}

private fun addSelfToGrid(grid: Map<Pair<String, String>, Long>): Map<Pair<String, String>, Long> {
  val newGrid = grid.toMutableMap()
  val diners = grid.keys.map { it.first }.distinct()
  diners.forEach { d ->
    newGrid["me" to d] = 0
    newGrid[d to "me"] = 0
  }
  return newGrid
}

private data class Relationship(
  val impacted: String,
  val change: Long,
  val cause: String
)
