package adventofcode.`2023`.`23`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val maze = File("src/adventofcode/2023/23/input.txt").readLines().map { it.toCharArray() }

  // Part 1
  println(findIterativeLongestPath(maze).size)
  // Part 2
  println(findLongestPath(Point(0, 1), Point(maze.size - 1, maze.first().size - 2), getSegments(maze)))
}

private typealias Point = Pair<Int, Int>

private fun findLongestPath(start: Point, end: Point, segments: Map<Point, List<Pair<Point, Int>>>): Int {
  var longest = 0
  // Prefer to search longest paths first.
  val paths = PriorityQueue<Triple<Point, Set<Point>, Int>> { (_, _, l1), (_, _, l2) -> l2 - l1}
  segments[start]?.forEach { (point, len) -> paths.add(Triple(point, setOf(start), len)) }
  while (paths.isNotEmpty()) {
    val (curr, visited, totalLen) = paths.poll()
    if (curr in visited) continue
    if (curr == end && longest < totalLen) {
      longest = totalLen
      continue
    }
    segments[curr]
        ?.filterNot { (p, _) -> p in visited }
        ?.forEach { (p, len) -> paths.add(Triple(p, visited + curr, len + totalLen)) }
  }
  return longest
}

private fun getSegments(maze: List<CharArray>): Map<Point, List<Pair<Point, Int>>> {
  val points = mutableSetOf(Pair(0, 1), Pair(maze.size - 1, maze.size - 2))
  for (r in maze.indices) for (c in maze.first().indices) {
    if (maze[r][c] != '#' && findAdjacentPoints(Pair(r, c)).filter { isWalkable(maze, it) }.size > 2) {
      points.add(Pair(r, c))
    }
  }
  return points.associateWith { p -> getPathsToNearestIntersections(maze, p) }
}

private fun getPathsToNearestIntersections(maze: List<CharArray>, start: Point): List<Pair<Point, Int>> {
  val adjacent = findAdjacentPoints(start).filter { isWalkable(maze, it) }.map { Pair(it, setOf(start)) }.toMutableList()
  val paths = mutableListOf<Pair<Point, Int>>()
  while (adjacent.isNotEmpty()) {
    val (point, visited) = adjacent.removeFirst()
    val nextPoints = findAdjacentPoints(point).filter { isWalkable(maze, it) && it !in visited }
    if (nextPoints.size > 1) {
      paths.add(Pair(point, visited.size))
    } else if (nextPoints.size == 1) {
      val next = nextPoints.first()
      if (next == Pair(0, 1) || next == Pair(maze.size-1, maze.first().size-2)) {
        paths.add(Pair(next, visited.size + 1))
      } else {
        adjacent.add(Pair(nextPoints.first(), visited + point))
      }
    }
  }
  return paths
}

private fun findIterativeLongestPath(maze: List<CharArray>): Set<Point> {
  val paths = PriorityQueue<Pair<Point, Set<Point>>> { (p1, s1), (p2, s2) ->
    if (s1.size != s2.size) s2.size - s1.size
    else p1.first + p1.second - p2.first - p2.second
  }
  paths.add(Pair(Point(0, 1), emptySet()))
  var longest = emptySet<Point>()
  while (paths.isNotEmpty()) {
    val (point, visited) = paths.poll()
    if (point == Pair(maze.size - 1, maze.last().size - 2)) {
      if (visited.size > longest.size) longest = visited
    } else {
      findNextPoints(maze, point)
          .filter { isWalkable(maze, it) && it !in visited }
          .forEach { p -> paths.add(Pair(p, visited + point)) }
    }
  }
  return longest
}

private fun findAdjacentPoints(current: Point): List<Point> {
  return listOf(
      Pair(current.first, current.second + 1),
      Pair(current.first + 1, current.second),
      Pair(current.first, current.second - 1),
      Pair(current.first - 1, current.second))
}

private fun findNextPoints(maze: List<CharArray>, current: Point): List<Point> {
  return when (maze[current.first][current.second]) {
    '>' -> listOf(Pair(current.first, current.second + 1))
    'v' -> listOf(Pair(current.first + 1, current.second))
    '<' -> listOf(Pair(current.first, current.second - 1))
    '^' -> listOf(Pair(current.first - 1, current.second))
    else -> findAdjacentPoints(current)
  }
}

private fun isWalkable(maze: List<CharArray>, current: Point): Boolean {
  return current.first in maze.indices
      && current.second in maze.first().indices
      && maze[current.first][current.second] != '#'
}
