package adventofcode.`2024`.`18`

import java.io.File
import java.util.PriorityQueue

/** https://adventofcode.com/2024/day/18 */
fun main() {
  val bytes = File("src/adventofcode/2024/18/input.txt").readLines().map { l ->
    l.split(",").map { it.toInt() }
  }
  val girth = 71
  println(findShortestPath(bytes.subList(0, 1024), girth))

  // Brute force, check until we hit it. Start from the end since non-paths will terminate faster.
  val firstBlocked = bytes.indices.reversed().first { findShortestPath(bytes.subList(0, it), girth) != null }
  println(bytes[firstBlocked].joinToString(","))
}

private fun findShortestPath(bytes: List<List<Int>>, dim: Int): Int? {
  val fallenBytes = bytes.map { it.first() to it.last() }.toSet()
  val shortestPathTo = mutableMapOf<Pair<Int, Int>, Int>()
  shortestPathTo[0 to 0] = 0
  val toVisit = PriorityQueue<Pair<Int, Int>>(Comparator.comparing { p -> shortestPathTo[p.first to p.second] ?: Int.MAX_VALUE })
  toVisit.add(0 to 0)
  while (toVisit.isNotEmpty()) {
    val (r, c) = toVisit.remove()
    if (r == dim-1 && c == dim - 1 ) break
    listOf(r-1 to c, r+1 to c, r to c-1, r to c+1).filter { (a, b) ->
      a in 0 until dim && b in 0 until dim && a to b !in fallenBytes
    }.forEach { (a, b) ->
      val d = 1 + (shortestPathTo[r to c] ?: throw IllegalStateException("No route to $r to $c"))
      if (d < (shortestPathTo[a to b] ?: Int.MAX_VALUE)) {
        shortestPathTo[a to b] = d
        toVisit.add(a to b)
      }
    }
  }
  return shortestPathTo[dim - 1 to dim - 1]
}
