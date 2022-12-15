package adventofcode.`2022`.`15`

import java.io.File
import kotlin.math.abs

fun main() {
  val match = Regex("[a-z ]*x=(-?\\d+), y=(-?\\d+):[a-z ]*x=(-?\\d+), y=(-?\\d+)")
  val sensorBeaconPairs = File("src/adventofcode/2022/15/input.txt")
    .readLines()
    .map { l ->
      val (sx, sy, bx, by) = match.find(l)?.destructured ?: error("Failed to parse $l")
      Pair(Pair(sx.toLong(), sy.toLong()), Pair(bx.toLong(), by.toLong()))
    }

  // Part 1
  var visibleSpots = setOf<Long>()
  val beaconsInRow = mutableSetOf<Long>()
  val row = 2_000_000L
  sensorBeaconPairs.forEach { (s, b) ->
    val visibleRange = getVisibleInRow(s, b, row)
    if (visibleRange != null) visibleSpots = visibleSpots.union(visibleRange)
    if (b.second == row) beaconsInRow.add(b.first)
  }
  println(visibleSpots.size - beaconsInRow.size)

  // Part 2
  // main idea: just check 1 further than the visible boundaries of each sensor
  val range = 0L..4_000_000
  for ((s, b) in sensorBeaconPairs) {
    val candidates = getPointsAtDistance(s, s-b+1, range, range)
    for (p in candidates) if (sensorBeaconPairs.all { (x, y) -> x - p > x - y }) {
      println(p.first * 4_000_000 + p.second)
      return
    }
  }
}

private fun getVisibleInRow(sensor: Pair<Long, Long>, beacon: Pair<Long, Long>, row: Long): LongRange? {
  val bDist = sensor - beacon
  val nearest = Pair(sensor.first, row)
  val nearestDist = sensor - nearest
  val vizRange = bDist - nearestDist
  if (vizRange < 0) return null
  return (sensor.first - vizRange) .. (sensor.first + vizRange)
}

private fun getPointsAtDistance(
  sensor: Pair<Long, Long>,
  d: Long,
  xRange: LongRange,
  yRange: LongRange): Set<Pair<Long, Long>> {
  val equidistantPoints = mutableSetOf<Pair<Long, Long>>()
  for (dx in -d..d) {
    val bx = sensor.first + dx
    if (bx in xRange) {
      val dy = d - abs(dx)
      if (sensor.second + dy in yRange) {
        equidistantPoints.add(Pair(bx, sensor.second + dy))
      }
      if (sensor.second - dy in yRange) {
        equidistantPoints.add(Pair(bx, sensor.second - dy))
      }
    }
  }
  return equidistantPoints
}

private operator fun Pair<Long, Long>.minus(other: Pair<Long, Long>): Long =
  abs(first - other.first) + abs(second - other.second)
