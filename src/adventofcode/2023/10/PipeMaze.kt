package adventofcode.`2023`.`10`

import java.io.File

fun main() {
  val (p1, p2) = explorePath(File("src/adventofcode/2023/10/input.txt").readLines())
  println(p1)
  println(p2)
}

private fun explorePath(maze: List<String>): Pair<Int, Int> {
  // Part 1
  val distances = Array(maze.size) { Array(maze.first().length) { Int.MAX_VALUE } }
  val start = getStart(maze)
  var (startType, check) = getStartMovements(start, maze)
  distances[start.first][start.second] = 0
  var d = 0
  while (check.isNotEmpty()) {
    d++
    val next = mutableSetOf<Pair<Int, Int>>()
    for ((r, c) in check) {
      if (distances[r][c] == Int.MAX_VALUE) {
        distances[r][c] = d
        next.addAll(getConnectedPoints(r, c, maze))
      }
    }
    check = next.filter { (r, c) -> distances[r][c] == Int.MAX_VALUE }
  }
  // Part 2
  var interiorCount = 0
  var prior: Char? = null
  for (r in maze.indices) {
    // When we've passed through an odd number of walls, we're on the interior.
    var parity = 0
    for (c in maze[r].indices) {
      if (distances[r][c] != Int.MAX_VALUE) {
        when (if (maze[r][c] == 'S') startType else maze[r][c]) {
          '|' -> parity++
          'F' -> prior = 'F'
          'L' -> prior = 'L'
          'J' -> { // Going through L--J is not passing through a wall of pipe
            if (prior == 'F') parity++
            prior = null
          }
          '7' -> { // Going through F--7 is not passing through a wall of pipe
            if (prior == 'L') parity++
            prior = null
          }
        }
      }
      if (distances[r][c] == Int.MAX_VALUE) {
        if (parity % 2 == 0) {
//          print(" ")
        } else {
//          print("*")
          interiorCount++
        }
//      } else {
//        print(maze[r][c])
      }
    }
//    println()
  }
  return Pair(d, interiorCount)
}

private fun getConnectedPoints(r: Int, c: Int, maze: List<String>): List<Pair<Int, Int>> {
  return when (maze[r][c]) {
    '|' -> listOf(Pair(r-1, c), Pair(r+1, c))
    '-' -> listOf(Pair(r, c-1), Pair(r, c+1))
    'L' -> listOf(Pair(r-1, c), Pair(r, c+1))
    'J' -> listOf(Pair(r-1, c), Pair(r, c-1))
    '7' -> listOf(Pair(r+1, c), Pair(r, c-1))
    'F' -> listOf(Pair(r+1, c), Pair(r, c+1))
    'S' -> emptyList()
    '.' -> error("How did we leave the pipe? Current position is [$r, $c]")
    else -> error("This isn't valid: [$r, $c] => ${maze[r][c]}")
  }
}

/** Returns the node type of the start, and a list of coordinates of connected adjacent pipe nodes. */
private fun getStartMovements(start: Pair<Int, Int>, maze: List<String>): Pair<Char, List<Pair<Int, Int>>> {
  val moves = mutableListOf<Pair<Int, Int>>()
  val (r, c) = start
  val possibleS = mutableSetOf('|', '-', 'L', 'J', '7', 'F')
  if (r-1 >= 0 && maze[r-1][c] in setOf('|', '7', 'F')) {
    moves.add(Pair(r-1, c))
    possibleS.removeAll(setOf('-', 'F', '7'))
  }
  if (r+1 < maze.size && maze[r+1][c] in setOf('|', 'L', 'J')) {
    moves.add(Pair(r+1, c))
    possibleS.removeAll(setOf('-', 'L', 'J'))
  }
  if (c-1 >= 0 && maze[r][c-1] in setOf('-', 'L', 'F')) {
    moves.add(Pair(r, c-1))
    possibleS.removeAll(setOf('|', 'F', 'L'))
  }
  if (c+1 < maze[r].length && maze[r][c+1] in setOf('-', 'J', '7')) {
    moves.add(Pair(r, c+1))
    possibleS.removeAll(setOf('|', 'J', '7'))
  }
  return Pair(possibleS.first(), moves)
}

private fun getStart(maze: List<String>): Pair<Int, Int> {
  maze.indices.forEach { r ->
    maze[r].indices.forEach { c ->
      if (maze[r][c] == 'S') return Pair(r, c)
    }
  }
  error("No starting point!")
}
