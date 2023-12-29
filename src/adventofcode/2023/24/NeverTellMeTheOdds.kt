package adventofcode.`2023`.`24`

import java.io.File
import kotlin.math.abs

fun main() {
  val hail = File("src/adventofcode/2023/24/input.txt").readLines().map { parseLine(it) }

  // Part 1
  println(countCollisions(hail))

  // Part 2
  println(bruteForce(hail))
}

private fun bruteForce(hail: List<Hail>): Long {
  // Ranges reduced to make this accessible. Guessed based on the input speeds.
  val xs = (-1000L..1000).toList()
  val ys = (-1000L..1000).toList()
  val zs = (-1000L..1000).toList()
  for (dx in xs) for (dy in ys) {
    val intersect = findCommonIntersection(dx, dy, zs, hail)
    if (intersect != null) return intersect.first + intersect.second + intersect.third
  }
  error("We should have found something :/")
}

private fun findCommonIntersection(dx: Long, dy: Long, zs: List<Long>, hail: List<Hail>): Triple<Long, Long, Long>? {
  // Main idea: if we subtract the speed of the thrown rock from all snowballs, we can consider it stuck in place and
  // just need to find where the snowballs collide. If they all collide at the same point, that's our starting position.
  var intersection: Pair<Long, Long>? = null
  val h1 = hail[0].adjustSpeed(dx, dy, 0)
  for (j in 1 until hail.size) {
    val h2 = hail[j].adjustSpeed(dx, dy, 0)
    val (xInter, yInter) = findCollisionPoint(h1, h2) ?: return null
    if (xInter.isNaN() || yInter.isNaN()) return null
    if (intersection == null) intersection = Pair(xInter.toLong(), yInter.toLong())
    else if (ehCloseEnough(xInter, yInter, intersection)) continue
    else if (xInter.toLong() != intersection.first || yInter.toLong() != intersection.second) return null
  }
  // Since we found where the x and y coordinates are, now we need to find the z.
  // Easiest way I can think of is to figure out how long it takes the first snowball to hit the start (t), then loop
  // through the possible z-speeds for the rock until we find one where multiple snowballs meet on the z-axis at the
  // first snowball's z at time t.
  val t = (intersection!!.first - h1.x) / h1.dx
  for (dz in zs) {
    val h1t = h1.adjustSpeed(0, 0, dz)
    val h2t = hail.last().adjustSpeed(dx, dy, dz)
    if (h2t.dz == 0L) continue
    val t2 = (h1t.z + t*h1t.dz - h2t.z) / h2t.dz
    if (h1t.x + t*h1t.dx == h2t.x + t2*h2t.dx) {
      return Triple(h1t.x + t*h1t.dx, h1t.y + t*h1t.dy, h1t.z + t*h1t.dz)
    }
  }
  error("Implement finding z now...")
}

// Rounding errors happen.
private fun ehCloseEnough(xInter: Double, yInter: Double, intersection: Pair<Long, Long>) =
    abs(xInter.toLong() - intersection.first) <= 1 && abs(yInter.toLong() - intersection.second) <= 1

private fun Hail.adjustSpeed(dx: Long, dy: Long, dz: Long): Hail {
  return Hail(x, y, z, this.dx - dx, this.dy - dy, this.dz - dz)
}
private fun countCollisions(hail: List<Hail>): Int {
  var count = 0
  for (i in hail.indices) for (j in i+1 until hail.size) {
    val collision = findCollisionPoint(hail[i], hail[j])
    if (collision != null && isCollisionInRange(collision)) count++
  }
  return count
}

private fun isCollisionInRange(collision: Pair<Double, Double>, start: Double = 2.0E14, end: Double = 4.0E14) =
  collision.first in start..end && collision.second in start..end

private fun findCollisionPoint(h1: Hail, h2: Hail): Pair<Double, Double>? {
  // Parallel check
  if (areLinesParallel(h1, h2)) {
    println("Lines $h1 and $h2 are parallel")
    return null
  }
  // We solve for t1 and t2 where the lines will intersect in the xy plane.
  // x1 + a*dx1 = x2 + b*dx2 => a*dx1 - b*dx2 = x2 - x1
  // y1 + a*dy1 = y2 + b*dy2 => a*dy1 - b*dy2 = y2 - y1
  val xEq = listOf(h1.dx, -h2.dx, h2.x - h1.x)
  val yEq = listOf(h1.dy, -h2.dy, h2.y - h1.y)
  // Cancel out the `a` term.
  val reduced = xEq.zip(yEq).map { (x, y) -> -(yEq.first()) * x + xEq.first() * y }
  val b = reduced.last().toDouble() / reduced[1]
  val a = (h2.x - h1.x + b*h2.dx) / h1.dx
  val x0 = h1.x + a*h1.dx
  val y0 = h1.y + a*h1.dy
  if (a < 0 || b < 0) return null
  return Pair(x0, y0)
}

private fun areLinesParallel(h1: Hail, h2: Hail): Boolean =
    h1.dx * h2.dy == h1.dy * h2.dx
        && h1.dz * h2.dx == h1.dx * h2.dz
        && h1.dy * h2.dz == h1.dz * h2.dy

private fun parseLine(str: String): Hail {
  val (start, end) = str.split(" @ ")
  val (x, y, z) = start.split(", ")
  val (dx, dy, dz) = end.split(", ")
  return Hail(
      x.trim().toLong(), y.trim().toLong(), z.trim().toLong(),
      dx.trim().toLong(), dy.trim().toLong(), dz.trim().toLong())
}

private data class Hail(
    val x: Long,
    val y: Long,
    val z: Long,
    val dx: Long,
    val dy: Long,
    val dz: Long)
