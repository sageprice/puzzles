package adventofcode.`2025`.`09`

import java.io.File
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * https://adventofcode.com/2025/day/9
 *
 * Heating up a bit here. Approach for part 2:
 * - key insight: if an edge point is fully in the interior of 2 points, we can't enclose a rectangle
 * - walk the edges to get all edge points
 * - index edge points by row (could be col)
 * - for each pair of points
 *   - get all rows between the points
 *   - get all edge points in those rows
 *   - if no edge points in interior, check area
 *
 * Later update: got a nice speed-up tip from Kailing for part 2. We can re-map the row and column
 * numbers into a smaller space so that we don't have to check as many points that might be in the
 * interior. If there are corners on K points, we can remap the K (strictly fewer really) X coords
 * to {0, ..., 2K}. We can't re-map to {1, ..., K} because then lines that go across the rectangle
 * would appear to be only on the edges of it (and therefore acceptable) in the remapped space.
 *
 * Making that changes brings runtime from over a minute to less than a second.
 */
fun main() {
  val input = File("src/adventofcode/2025/09/input.txt").readLines()
    .map {
      val (r, c) = it.split(",")
      r.toLong() to c.toLong()
    }

  println(findLargestRectangle(input))

  // Remap the row and col numbers into a smaller space so we have fewer points to check.
  val rMap = remapIndices(input.map { it.first })
  val cMap = remapIndices(input.map { it.second })
  val remappedPoints = input.map { (r, c) -> rMap[r]!! to cMap[c]!! }
  // The actual algorithm happens here.
  val rects = findLargestEnclosedRectangle2(remappedPoints)
  // Invert the mapping so we can do the area calculation.
  val invertedRMap = rMap.entries.associate { (x, y) -> y to x }
  val invertedCMap = cMap.entries.associate { (x, y) -> y to x }
  val maxArea = rects.maxOf { (p1, p2) ->
    val p1i = invertedRMap[p1.first]!! to invertedCMap[p1.second]!!
    val p2i = invertedRMap[p2.first]!! to invertedCMap[p2.second]!!
    getArea(p1i, p2i)
  }
  println(maxArea)
}

private fun remapIndices(points: List<Long>): Map<Long, Int> {
  return points.distinct().sorted().mapIndexed { idx, i ->
    i to 2*idx
  }.associate { it }
}

private fun findLargestRectangle(points: List<Pair<Long, Long>>): Long {
  var maxArea = 0L
  for (i in points.indices) for (j in i+1 until points.size) {
    val (r1, c1) = points[i]
    val (r2, c2) = points[j]
    val area = (1+abs(r1-r2)) * (1+abs(c1-c2))
    if (area > maxArea) {
      maxArea = area
    }
  }
  return maxArea
}

private fun walkEdges(points: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
  val edgePoints = mutableListOf<Pair<Int, Int>>()
  for (i in points.indices) {
    val (r1, c1) = points[i]
    val (r2, c2) = if (i+1 == points.size) {
      points[0]
    } else {
      points[i+1]
    }
    val rMin = min(r1, r2)
    val rMax = max(r1, r2)
    val cMin = min(c1, c2)
    val cMax = max(c1, c2)
    for (r in rMin..rMax) for (c in cMin..cMax) {
      edgePoints.add(r to c)
    }
  }
  return edgePoints
}

private fun getPointsInRows(ps: List<Pair<Int, Int>>): Map<Int, Set<Pair<Int, Int>>> {
  val pointsInRows = mutableMapOf<Int, MutableSet<Pair<Int, Int>>>()
  for (p in ps) {
    if (p.first !in pointsInRows) {
      pointsInRows[p.first] = mutableSetOf()
    }
    pointsInRows[p.first]?.add(p)
  }
  return pointsInRows
}

private fun findLargestEnclosedRectangle2(points: List<Pair<Int, Int>>): List<Pair<Pair<Int, Int>, Pair<Int, Int>>> {
  val rects = mutableListOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()
  val edgePoints = walkEdges(points)
  val pointsInRows = getPointsInRows(edgePoints)
  for (i in points.indices) {
    val a = points[i]
    test@for (j in i+1 until points.size) {
      val b = points[j]
      val rMin = min(a.first, b.first)
      val rMax = max(a.first, b.first)
      val cMin = min(a.second, b.second)
      val cMax = max(a.second, b.second)
      for (r in (rMin+1) until rMax) {
        for (p in pointsInRows[r] ?: emptySet()) {
          if (p.second in (cMin+1) until cMax) {
            continue@test
          }
        }
      }
      rects.add(Pair(a, b))
    }
  }
  return rects
}

private fun getArea(p1: Pair<Long, Long>, p2: Pair<Long, Long>): Long {
  val (r1, c1) = p1
  val (r2, c2) = p2
  return (1+abs(r1-r2)) * (1+abs(c1-c2))
}
