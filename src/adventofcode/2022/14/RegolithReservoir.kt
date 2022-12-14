package adventofcode.`2022`.`14`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val input = File("src/adventofcode/2022/14/input.txt")
    .readLines()
    .map {
      it.split(" -> ").map { p ->
        val (x, y) = p.split(",")
        Pair(x.toInt(), y.toInt())
    } }

  val cave = mutableMapOf<Pair<Int, Int>, String>()
  val allRocks = input.flatten()
  val minWidth = allRocks.minOf { it.first }
  val maxWidth = allRocks.maxOf { it.first }
  val minDepth = allRocks.minOf { it.second }
  val maxDepth = allRocks.maxOf { it.second }

  // Set up the rocks in the cave
  input.forEach { l ->
    for (i in 1 until l.size) {
      val (x1, y1) = l[i - 1]
      val (x2, y2) = l[i]
      (min(x1, x2)..max(x1, x2)).forEach { x ->
        (min(y1, y2)..max(y1, y2)).forEach { y -> cave[Pair(x, y)] = "#" }
      }
    }
  }
  // Part 1
  val depthRange = -1..maxDepth + 1
  println(fillCaveWithSand(cave, depthRange).count { (_, b) -> b == "o" })

  // Part 2
  // Just add a floor. This should be wide enough...
  for (x in minWidth - 1000..maxWidth + 1000) cave[Pair(x, maxDepth + 2)] = "#"
  println(fillCaveWithSand(cave, depthRange).count { (_, b) -> b == "o" })
}

//private fun printCave(cave: Map<Pair<Int, Int>, String>, widthRange: IntRange, depthRange: IntRange) {
//  println(cave)
//  (depthRange).forEach { y ->
//    (widthRange).forEach { x ->
//      print(cave[Pair(x, y)] ?: ".")
//    }
//    println()
//  }
//}

private fun fillCaveWithSand(cave: Map<Pair<Int, Int>, String>, depthRange: IntRange): Map<Pair<Int, Int>, String> {
  val sandyCave = cave.toMutableMap()
  var newSand: Pair<Int, Int>? = dropSand(sandyCave, depthRange)
  while (newSand != null) {
    sandyCave[newSand] = "o"
    newSand = dropSand(sandyCave, depthRange)
  }
  return sandyCave
}

private fun dropSand(cave: Map<Pair<Int, Int>, String>, depthRange: IntRange): Pair<Int, Int>? {
  var x = 500
  var y = 0
  while (true) {
    // Exit when falling infinitely
    if (y !in depthRange) return null
    // Exit when we've gummed up the entry
    if (cave.containsKey(Pair(x, y))) return null
    if (!cave.containsKey(Pair(x, y + 1))) {
      y += 1
      continue
    }
    if (!cave.containsKey(Pair(x - 1, y + 1))) {
      x -= 1
      y += 1
      continue
    }
    if (!cave.containsKey(Pair(x + 1, y + 1))) {
      x += 1
      y += 1
      continue
    }
    break
  }
  return Pair(x, y)
}
