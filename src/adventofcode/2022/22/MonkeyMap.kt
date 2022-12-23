package adventofcode.`2022`.`22`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/22/input.txt")
    .readText()

  val (inputGrid, inputMoves) = input.split("\r\n\r\n")
  val gridWidth = inputGrid.indexOfFirst { it == '\r' } // frickin windows
  val grid = inputGrid.split("\r\n").map { r ->
    if (r.length < gridWidth) r.padEnd(gridWidth, ' ')
    else r
  }
  val moves = inputMoves.replace("R", " R ").replace("L", " L ").split(" ")

  val (x, y, cursor) = walkWithWraparound(grid, moves)
  println(4 * x + 1000 * y + cursor)

  val (cx, cy, cc) = walkCube(grid, moves)
  println(4 * cx + 1000 * cy + cc)
}

private fun walkWithWraparound(grid: List<String>, moves: List<String>): Triple<Int, Int, Int> {
  var posX = grid.first().indexOfFirst { it == '.' || it == '#'}
  var posY = 0
  var dx = 1
  var dy = 0
  for (move in moves) {
    when (move) {
      "R" -> {
        val t = dy
        dy = dx
        dx = -t
      }
      "L" -> {
        val t = dy
        dy = -dx
        dx = t
      }
      else -> {
        repeat(move.toInt()) {
          val p = takeStep(grid, posX, posY, dx, dy)
          posX = p.first
          posY = p.second
        }
      }
    }
  }
  return Triple(
    posX+1,
    posY+1,
    when(getCursor(dx, dy)) {
      '>' -> 0
      'v' -> 1
      '<' -> 2
      else -> 3
    })
}

private fun takeStep(grid: List<String>, x: Int, y: Int, dx: Int, dy: Int): Pair<Int, Int> {
  if (dx == -1 && (x + dx < 0 || grid[y][x + dx] == ' ')) {
    val wrappedIndex = grid[y].indexOfLast { it == '.' || it == '#' }
    return if (grid[y][wrappedIndex] == '.') Pair(wrappedIndex, y)
    else Pair(x, y)
  }
  if (dx == 1 && (x+dx >= grid.first().length || grid[y][x + dx] == ' ')) {
    val wrappedIndex = grid[y].indexOfFirst { it == '.' || it == '#' }
    return if (grid[y][wrappedIndex] == '.') Pair(wrappedIndex, y)
    else Pair(x, y)
  }
  if (dy == -1 && (y + dy < 0 || grid[y+dy][x] == ' ')) {
    val wrappedIndex = grid.map { it[x] }.indexOfLast { it == '.' || it == '#' }
    return if (grid[wrappedIndex][x] == '.') Pair(x, wrappedIndex)
    else Pair(x, y)
  }
  if (dy == 1 && (y + dy >= grid.size || grid[y+dy][x] == ' ')) {
    val wrappedIndex = grid.map { it[x] }.indexOfFirst { it == '.' || it == '#' }
    return if (grid[wrappedIndex][x] == '.') Pair(x, wrappedIndex)
    else Pair(x, y)
  }
  return if (grid[y+dy][x+dx] == '.') Pair(x+dx, y+dy)
  else Pair(x, y)
}

private fun walkCube(grid: List<String>, moves: List<String>): Triple<Int, Int, Int> {
  val transitions = buildCubeTransitions()
  var posX = grid.first().indexOfFirst { it == '.' || it == '#'}
  var posY = 0
  var cursor = '>'
  for (move in moves) {
    when (move) {
      "R" -> {
        cursor = when(cursor) {
          '>' -> 'v'
          'v' -> '<'
          '<' -> '^'
          '^' -> '>'
          else -> error("Invalid cursor")
        }
      }
      "L" -> {
        cursor = when(cursor) {
          '>' -> '^'
          'v' -> '>'
          '<' -> 'v'
          '^' -> '<'
          else -> error("Invalid cursor")
        }
      }
      else -> {
        repeat(move.toInt()) {
          val p = takeCubeStep(grid, transitions, posX, posY, cursor)
          posX = p.first
          posY = p.second
          cursor = p.third
        }
      }
    }
  }
  return Triple(
    posX+1,
    posY+1,
    when(cursor) {
      '>' -> 0
      'v' -> 1
      '<' -> 2
      else -> 3
    })
}

private fun takeCubeStep(
  grid: List<String>,
  transitions: Map<Pair<Int, Int>, Triple<Int, Int, Char>>,
  x: Int, y: Int, cursor: Char): Triple<Int, Int, Char> {
  val (dx, dy) = when(cursor) {
    '>' -> Pair(1, 0)
    'v' -> Pair(0, 1)
    '<' -> Pair(-1, 0)
    '^' -> Pair(0, -1)
    else -> error("Invalid cursor: $cursor")
  }
  val stepAroundEdge = transitions[Pair(x+dx, y+dy)]
  if (stepAroundEdge != null) {
    val next = grid[stepAroundEdge.second][stepAroundEdge.first]
    return if (next == '.') stepAroundEdge
    else Triple(x, y, cursor)
  }
  return if (grid[y+dy][x+dx] == '.') Triple(x+dx, y+dy, cursor)
  else Triple(x, y, cursor)

}


private fun buildCubeTransitions(): Map<Pair<Int, Int>, Triple<Int, Int, Char>> {
  // HARDCODED
  // A more sustainable solution would be to find a corner where two edges are zipped together, and walk the outer
  // boundary from there. Can either use two pointers to trace and pair up points, or collect all the edge points and
  // reconstruct the pairs by flipping and zipping at the midpoint. Either way, it was easier to do this. Hopefully I'll
  // come back and take the generic approach in future.
  val transitions = mutableMapOf<Pair<Int, Int>, Triple<Int, Int, Char>>()
  for (i in 0 until 50) {
    // mid-top go up to left of left-bottom
    transitions[Pair(50 + i, -1)] = Triple(0, 150+i, '>')
    transitions[Pair(-1, 150+i)] = Triple(50+i, 0, 'v')

    // right-top go up to bottom of left-bottom
    transitions[Pair(100+i, -1)] = Triple(i, 200-1, '^')
    transitions[Pair(i, 200)] = Triple(100+i, 0, 'v')

    // left-third go up to left of mid-second
    transitions[Pair(i, 99)] = Triple(50, 50+i, '>')
    transitions[Pair(49, 50+i)] = Triple(i, 100, 'v')

    // mid-top go left to left-third
    transitions[Pair(49, i)] = Triple(0, 149-i, '>')
    transitions[Pair(-1, 100+i)] = Triple(50, 49-i, '>')

    // mid-second go right to right-top
    transitions[Pair(100, 50+i)] = Triple(100+i, 49, '^')
    transitions[Pair(100+i, 50)] = Triple(99, 50+i, '<')

    // right-top go right to mid-third
    transitions[Pair(150, i)] = Triple(99, 149-i, '<')
    transitions[Pair(100, 100+i)] = Triple(149, 49-i, '<')

    // left-bottom go right to mid-third
    transitions[Pair(50, 150+i)] = Triple(50+i, 149, '^')
    transitions[Pair(50+i, 150)] = Triple(49, 150+i, '<')
  }
  return transitions
}

private fun getCursor(dx: Int, dy: Int): Char {
  if (dx == 1) return '>'
  if (dx == -1) return '<'
  if (dy == 1) return 'v'
  if (dy == -1) return '^'
  else error("WHAT HAPPENED TO MY ORIENTATION $dx, $dy")
}

//private fun printGrid(grid: List<String>, x: Int, y: Int, cursor: Char) {
//  println("===========================================================================")
//  println("===========================================================================")
//  for (i in grid.indices) {
//    for (j in grid[i].indices) {
//      if (i == y && j == x) print(cursor)
//      else print(grid[i][j])
//    }
//    println()
//  }
//}