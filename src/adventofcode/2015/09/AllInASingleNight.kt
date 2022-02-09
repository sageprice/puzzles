package adventofcode.`2015`.`09`

import java.io.File

fun main() {
  val lines = File("src/adventofcode/2015/09/input.txt").readLines()
  val distances = mutableMapOf<Pair<String, String>, Long>()
  val towns = mutableSetOf<String>()
  // I am so lazy
  for (line in lines) {
    val (ts, d) = line.split(" = ")
    val (left, right) = ts.split(" to ")
    distances[Pair("", left)] = 0
    distances[Pair("", right)] = 0
    distances[Pair(left, "")] = 0
    distances[Pair(right, "")] = 0
    distances[Pair(left, right)] = d.toLong()
    distances[Pair(right, left)] = d.toLong()
    towns.add(left)
    towns.add(right)
  }
  println(getMinDistance("", towns, distances))
  println(getMaxDistance("", towns, distances))
}

fun getMinDistance(lastTown: String, townsToVisit: Set<String>, distances: Map<Pair<String, String>, Long>): Long {
  return if (townsToVisit.isEmpty()) 0 else townsToVisit.minOf { town ->
    distances[Pair(town, lastTown)]!! + getMinDistance(town, townsToVisit - town, distances)
  }
}

fun getMaxDistance(lastTown: String, townsToVisit: Set<String>, distances: Map<Pair<String, String>, Long>): Long {
  return if (townsToVisit.isEmpty()) 0 else townsToVisit.maxOf { town ->
    distances[Pair(town, lastTown)]!! + getMaxDistance(town, townsToVisit - town, distances)
  }
}