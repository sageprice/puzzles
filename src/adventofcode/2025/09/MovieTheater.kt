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
 * Still runs quite slowly, maybe a minute or two. Could optimize by checking smaller of sets for
 * row-indexed or col-indexed edge points.
 */
fun main() {
  val input = File("src/adventofcode/2025/09/input.txt").readLines()
    .map {
      val (r, c) = it.split(",")
      r.toLong() to c.toLong()
    }

  println(findLargestRectangle(input))
  println(findLargestEnclosedRectangle2(input))
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

private fun walkEdges(points: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
  val edgePoints = mutableListOf<Pair<Long, Long>>()
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

private fun getPointsInRows(ps: List<Pair<Long, Long>>): Map<Long, Set<Pair<Long, Long>>> {
  val pointsInRows = mutableMapOf<Long, MutableSet<Pair<Long, Long>>>()
  for (p in ps) {
    if (p.first !in pointsInRows) {
      pointsInRows[p.first] = mutableSetOf()
    }
    pointsInRows[p.first]?.add(p)
  }
  return pointsInRows
}

private fun getPointsInCols(ps: List<Pair<Long, Long>>): Map<Long, Set<Pair<Long, Long>>> {
  val pointsInCols = mutableMapOf<Long, MutableSet<Pair<Long, Long>>>()
  for (p in ps) {
    if (p.second !in pointsInCols) {
      pointsInCols[p.second] = mutableSetOf()
    }
    pointsInCols[p.second]?.add(p)
  }
  return pointsInCols
}

private fun findLargestEnclosedRectangle2(points: List<Pair<Long, Long>>): Long {
  var maxArea = 0L
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
      val area = getArea(a, b)
//      println("Rectangle $a, $b has no interior points, area of $area")
      if (area > maxArea) maxArea = area
    }
  }
  return maxArea
}

private fun getArea(p1: Pair<Long, Long>, p2: Pair<Long, Long>): Long {
  val (r1, c1) = p1
  val (r2, c2) = p2
  return (1+abs(r1-r2)) * (1+abs(c1-c2))
}
