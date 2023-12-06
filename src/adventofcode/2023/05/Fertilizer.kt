package adventofcode.`2023`.`05`

import java.io.File

fun main() = Day5().run()

private class Day5 : Runnable {

  override fun run() {
    val inputSections = File("src/adventofcode/2023/05/input.txt").readText().split("\r\n\r\n")
    val seeds =
      inputSections
        .first()
        .split(":")
        .last()
        .trim()
        .split(" ")
        .filter { it.isNotEmpty() }
        .map { it.toLong() }

    val almanacMaps = inputSections.drop(1).map { parseXYMap(it) }

    // Part 1
    println(seeds.minOf { getSeedLocation(it, almanacMaps) })

    // Part 2
    // Stupid brute force b/c 1B numbers isn't _that_ much
    println(seeds.chunked(2).minOf { (a, b) ->
      (a..a+b).minOf { getSeedLocation(it, almanacMaps) }
    })
  }

  private fun getSeedLocation(s: Long, ams: List<AlmanacMap>): Long {
    var start = s
    for (am in ams) {
      start = findNextValue(start, am.maps)
    }
    return start
  }

  private fun findNextValue(x: Long, maps: List<MapRule>): Long {
    for ((d, s, r) in maps) {
      if (s > x) break
      if (x in s until s + r) {
        return d + x - s
      }
    }
    return x
  }

  private fun parseXYMap(input: String): AlmanacMap {
    val lines = input.split("\r\n")
    val mapTypeParts = lines.first().split(" ").first().split("-")
    val sourceType = mapTypeParts.first()
    val destType = mapTypeParts.last().trim()
    return AlmanacMap(
      sourceType,
      destType,
      lines.subList(1, lines.size).map { parseMapRule(it) }.sortedBy { it.sourceStart }
    )
  }

  private fun parseMapRule(input: String): MapRule {
    val parts = input.split(" ").filter { it.isNotEmpty() }.map { it.toLong() }
    return MapRule(parts[0], parts[1], parts[2])
  }
}

private data class AlmanacMap(
  val sourceType: String,
  val destType: String,
  val maps: List<MapRule>
)

private data class MapRule(val destStart: Long, val sourceStart: Long, val range: Long)