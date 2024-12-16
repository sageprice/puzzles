package adventofcode.`2024`.`16`

import java.io.File
import java.util.PriorityQueue
import kotlin.math.min

/** https://adventofcode.com/2024/day/16 */
fun main() {
  val maze = File("src/adventofcode/2024/16/input.txt").readLines()

  println(walkMaze(maze))
  println(reconstructPaths(maze).size)
}

private val cache = mutableMapOf<Reindeer, Pair<Long, MutableSet<Reindeer>>>()

private fun walkMaze(maze: List<String>): Long {
  val start = getLocation(maze, 'S')
  val end = getLocation(maze, 'E')
  val heap = PriorityQueue<Pair<Reindeer, Pair<Long, Reindeer?>>>(Comparator.comparing { (_, cost) -> cost.first })
  heap.add(Reindeer(start.first, start.second, 0, 1) to (0L to null))
  while (heap.isNotEmpty()) {
    val (r0, p) = heap.remove()
    val (cost, prior) = p
    if (r0 !in cache) {
      cache[r0] = cost to (if (prior != null) mutableSetOf(prior) else mutableSetOf())
    } else if (r0 in cache) {
      if (cache[r0]!!.first > cost) {
        cache[r0] = cost to (if (prior != null) mutableSetOf(prior) else mutableSetOf())
      } else if (cache[r0]!!.first == cost && prior != null) {
        cache[r0]?.second?.add(prior)
      } else {
        continue
      }
    }
    if (r0.r to r0.c == end) continue
    heap.add(Reindeer(r0.r, r0.c, -r0.dc, r0.dr) to (cost + 1000 to r0))
    heap.add(Reindeer(r0.r, r0.c, r0.dc, -r0.dr) to (cost + 1000 to r0))
    if (maze[r0.r + r0.dr][r0.c + r0.dc] != '#') {
      heap.add(Reindeer(r0.r + r0.dr, r0.c + r0.dc, r0.dr, r0.dc) to (cost + 1 to r0))
    }
  }
  return min(
    cache[Reindeer(end.first, end.second, -1, 0)]?.first ?: Long.MAX_VALUE,
    cache[Reindeer(end.first, end.second, 0, 1)]?.first ?: Long.MAX_VALUE)
}

private fun reconstructPaths(maze: List<String>): Set<Pair<Int, Int>> {
  val end = getLocation(maze, 'E')
  val seats = mutableSetOf<Pair<Int, Int>>()
  // Note: assumes all equal cost paths come from same final direction.
  val lastReindeer = listOf(Reindeer(end.first, end.second, -1, 0),
    Reindeer(end.first, end.second, 0, 1)).minBy { cache[it]?.first ?: Long.MAX_VALUE }
  val toWalk = mutableListOf(lastReindeer)
  while (toWalk.isNotEmpty()) {
    val next = toWalk.removeFirst()
    seats.add(next.r to next.c)
    toWalk.addAll(cache[next]?.second ?: emptyList())
  }
  return seats
}

private fun getLocation(maze: List<String>, char: Char): Pair<Int, Int> {
  for (r in maze.indices) for (c in maze[r].indices) {
    if (maze[r][c] == char) return r to c
  }
  throw IllegalArgumentException("Could not find $char in maze")
}

private data class Reindeer(
  val r: Int, val c: Int, val dr: Int, val dc: Int
)
