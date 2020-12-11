import java.io.File

import kotlin.math.min

fun main() {
  val grid: List<List<Char>> =
      File("src/adventofcode/2020/11/input.txt")
          .readLines()
          .map { row -> row.map { it } }

  // Part 1
  println("Part 1: " + stabilizeSeating(grid, getVisibleSeats1(grid), 4).flatten().count { it == '#' })

  // Part 2
  println("Part 2: " + stabilizeSeating(grid, getVisibleSeats2(grid), 5).flatten().count { it == '#' })
}

fun stabilizeSeating(
    grid: List<List<Char>>,
    vizMap: Map<Pair<Int, Int>, List<Pair<Int, Int>>>,
    tooManyNeighbors: Int
): List<List<Char>> {
  var lastGrid: List<List<Char>> = grid.toMutableList()
  var nextGrid = iterate(lastGrid, vizMap, tooManyNeighbors)
  while (nextGrid != lastGrid) {
    lastGrid = nextGrid
    nextGrid = iterate(lastGrid, vizMap, tooManyNeighbors)
  }
  return nextGrid
}

fun iterate(
    grid: List<List<Char>>,
    vizMap: Map<Pair<Int, Int>, List<Pair<Int, Int>>>,
    tooManyNeighbors: Int
): List<List<Char>> {
  val nextGrid: List<MutableList<Char>> = grid.map { it.toMutableList() }
  for (x in nextGrid.indices) {
    for (y in nextGrid[0].indices) {
        val vizCount = vizMap[Pair(x, y)]!!.count { (a, b) -> grid[a][b] == '#' }
        if (grid[x][y] == '.') nextGrid[x][y] = '.'
        else if (grid[x][y] == 'L' && vizCount == 0) nextGrid[x][y] = '#'
        else if (grid[x][y] == '#' && vizCount >= tooManyNeighbors) nextGrid[x][y] = 'L'
        else nextGrid[x][y] = grid[x][y]
    }
  }
  return nextGrid
}

private fun getVisibleSeats1(grid: List<List<Char>>): Map<Pair<Int, Int>, List<Pair<Int, Int>>> {
  val vizMap = emptyVizMap(grid)
  val xLength = grid.size
  val yLength = grid[0].size
  for (x in grid.indices) {
    for (y in grid[x].indices) {
      if (grid[x][y] == '.') continue
      for (dx in -1..1) {
        if (x + dx < 0 || x + dx >= xLength) continue
        for (dy in -1..1) {
          if (y + dy < 0 || y + dy >= yLength || (dx == 0 && dy == 0)) continue
          if (grid[x][y] != '.') vizMap[Pair(x, y)]!!.add(Pair(x + dx,y + dy))
        }
      }
    }
  }
  return vizMap
}

fun getVisibleSeats2(grid: List<List<Char>>): Map<Pair<Int, Int>, List<Pair<Int, Int>>> {
  val vizMap = emptyVizMap(grid)
  for (x in grid.indices) {
    for (y in grid[0].indices) {
      if (grid[x][y] == '.') continue
      for (i in 1 until grid.size - x) {
        if (grid[x+i][y] != '.') {
          vizMap[Pair(x, y)]!!.add(Pair(x+i, y))
          vizMap[Pair(x+i, y)]!!.add(Pair(x, y))
          break
        }
      }
      for (i in 1 until grid[0].size - y) {
        if (grid[x][y+i] != '.') {
          vizMap[Pair(x, y)]!!.add(Pair(x, y+i))
          vizMap[Pair(x, y+i)]!!.add(Pair(x, y))
          break
        }
      }
      for (i in 1 until min(grid.size - x, grid[0].size-y)) {
        if (grid[x+i][y+i] != '.') {
          vizMap[Pair(x, y)]!!.add(Pair(x+i, y+i))
          vizMap[Pair(x+i, y+i)]!!.add(Pair(x, y))
          break
        }
      }
      for (i in 1 until min(grid.size-x, y+1)) {
        if (grid[x+i][y-i] != '.') {
          vizMap[Pair(x, y)]!!.add(Pair(x+i, y-i))
          vizMap[Pair(x+i, y-i)]!!.add(Pair(x, y))
          break
        }
      }
    }
  }
  return vizMap
}

private fun emptyVizMap(grid: List<List<Char>>): MutableMap<Pair<Int, Int>, MutableList<Pair<Int, Int>>> {
  val vizMap = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Int, Int>>>()
  for (x in grid.indices) {
    for (y in grid[0].indices) {
      vizMap[Pair(x, y)] = mutableListOf()
    }
  }
  return vizMap
}