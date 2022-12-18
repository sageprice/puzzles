package adventofcode.`2022`.`18`

import java.io.File
import kotlin.math.abs

fun main() {
  val input = File("src/adventofcode/2022/18/input.txt").readLines()
    .map { val (a, b, c) = it.split(","); Triple(a.toInt(), b.toInt(), c.toInt()) }

  // Part 1
  var c = 0
  for (i in input.indices) for (j in i+1 until input.size) if (areAdjacent(input[i], input[j])) c++
  println(6*input.size - 2*c)

  // Part 2
  // Idea: traverse the area outside the obsidian. Count how many sides are visible.
  val minx = input.minOf { it.first }
  val maxx = input.maxOf { it.first }
  val miny = input.minOf { it.second }
  val maxy = input.maxOf { it.second }
  val minz = input.minOf { it.third }
  val maxz = input.maxOf { it.third }

  var visibleSides = 0
  val seen = mutableSetOf<Triple<Int, Int, Int>>()
  val exteriorPoints = mutableListOf(Triple(minx-1, miny-1, minz-1))
  while (exteriorPoints.isNotEmpty()) {
    val p = exteriorPoints.removeFirst()
    // skip previously seen and actual points in set
    if (seen.contains(p) || input.contains(p)) continue
    seen.add(p)
    getNeighbors(p).forEach { n ->
      if (n in input) visibleSides++
      else if (n !in seen) {
        val (x, y, z) = n
        if (x in minx-1..maxx+1 && y in miny-1..maxy+1 && z in minz-1..maxz+1) exteriorPoints.add(n)
      }
    }
  }
  println(visibleSides)
}

private fun areAdjacent(a: Triple<Int, Int, Int>, b: Triple<Int, Int, Int>): Boolean {
  val (x1, y1, z1) = a
  val (x2, y2, z2) = b
  val f1 = x1==x2
  val f2 = y1==y2
  val f3 = z1==z2
  if (listOf(f1, f2, f3).count { it } != 2) return false
  if (!f1 && abs(x1-x2) == 1) return true
  if (!f2 && abs(y1-y2) == 1) return true
  if (!f3 && abs(z1-z2) == 1) return true
  return false
}

private fun getNeighbors(p: Triple<Int, Int, Int>): List<Triple<Int, Int, Int>> {
  val (x, y, z) = p
  return mutableListOf(
    Triple(x-1, y, z),
    Triple(x+1, y, z),
    Triple(x, y-1, z),
    Triple(x, y+1, z),
    Triple(x, y, z-1),
    Triple(x, y, z+1))
}
