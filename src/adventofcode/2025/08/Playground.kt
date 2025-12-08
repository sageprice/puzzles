package adventofcode.`2025`.`08`

import java.io.File
import java.util.PriorityQueue
import kotlin.math.sqrt

/**
 * https://adventofcode.com/2025/day/8
 *
 * Main idea: use a heap to keep track of distances between boxes, use a map from box -> circuit
 * to track which circuits boxes belong to. Then keep popping pairs off the heap, and either
 * combine their circuits, add to one circuit, or create a new circuit as needed.
 */
fun main() {
  val boxes = File("src/adventofcode/2025/08/input.txt").readLines()
    .map {
      val (a, b, c) = it.split(",").map { s -> s.toInt() }
      Triple(a, b, c)
    }

  val (circuits, _) = connectCircuits(boxes, 1000)
  println(circuits.sortedByDescending { it.size }.take(3).fold(1) { acc, s -> acc * s.size })
  val (_, finalLink) = connectCircuits(boxes, null)
  println(finalLink!!.first.first * finalLink.second.first.toLong())
}

private fun connectCircuits(boxes: List<Box>, k: Int?): Pair<List<Set<Box>>, Pair<Box, Box>?> {
  val distances = PriorityQueue<Pair<Box, Box>> { p1, p2 ->
    distance(p1.first, p1.second) - distance(p2.first, p2.second)
  }
  for (i in boxes.indices) for (j in i+1 until boxes.size) {
    distances.add(boxes[i] to boxes[j])
  }
  val circuits = mutableMapOf<Box, MutableSet<Box>>()
  repeat(k ?: (boxes.size * boxes.size)) {
    if (distances.isEmpty()) {
      throw IllegalStateException("Ran out of links")
    }
    val (b1, b2) = distances.poll()
    val c1 = circuits[b1]
    val c2 = circuits[b2]
    if (c1 == null && c2 == null) {
      val circuit = mutableSetOf(b1, b2)
      circuits[b1] = circuit
      circuits[b2] = circuit
    } else if (c1 == null && c2 != null) {
      c2.add(b1)
      c2.add(b2)
      circuits[b1] = c2
    } else if (c1 != null && c2 == null) {
      c1.add(b1)
      c1.add(b2)
      circuits[b2] = c1
    }
    else if (c1 != null && c2 != null) {
      if (b1 !in c2) {
        val allBoxes = c1 + c2
        val mutAllBoxes = allBoxes.toMutableSet()
        for (box in allBoxes) {
          circuits[box] = mutAllBoxes
        }
      }
    }
    if (k == null) {
      val c = circuits.values.distinct().maxByOrNull { it.size }!!
      if (c.size == boxes.size) {
        return circuits.values.distinct() to (b1 to b2)
      }
    }
  }
  return circuits.values.distinct() to null
}

private typealias Box = Triple<Int, Int, Int>
private fun distance(b1: Box, b2: Box): Int {
  val dx = b1.first - b2.first.toDouble()
  val dy = b1.second - b2.second.toLong()
  val dz = b1.third - b2.third.toLong()
  val d = dx*dx + dy*dy + dz*dz
  if (d < 0) throw IllegalStateException("Bad distance")
  return sqrt(dx*dx + dy*dy + dz*dz).toInt()
}
