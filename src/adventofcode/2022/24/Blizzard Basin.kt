package adventofcode.`2022`.`24`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val valleyBitMask: List<List<Int>> =
    File("src/adventofcode/2022/24/input.txt").readLines().map {
      it.map { c ->
        when (c) {
          '#' -> 16
          '>' -> 1
          'v' -> 2
          '<' -> 4
          '^' -> 8
          else -> 0
      }
    }
  }
  val exitRow = valleyBitMask.size - 1
  val exitColumn = valleyBitMask[0].size - 2

  // Part 1
  val exitState = navigate(ValleyState(valleyBitMask, 0, 1, 0), exitRow, exitColumn)
  println(exitState.t)

  // Part 2
  val returnToStartState =
    navigate(exitState, 0, 1)
  val returnToExitState =
    navigate(returnToStartState, exitRow, exitColumn)
  println(returnToExitState.t)
}

private fun navigate(initialState: ValleyState, endR: Int, endC: Int): ValleyState {
  val states = PriorityQueue<ValleyState>(Comparator.comparing { it.t })
  states.add(initialState)
  val cache = mutableSetOf<Triple<Int, Int, Int>>()
  while (true) {
    val (valley, r, c, t) = states.poll()
    val key = Triple(r, c, t)
    if (key in cache) { continue }
    cache.add(key)
    val nextValley = nextValley(valley)
    val nextPositions = nextPositions(nextValley, r, c)
    for ((r1, c1) in nextPositions) {
      if (r1 == endR && c1 == endC) return ValleyState(nextValley, r1, c1, t+1)
      else states.add(ValleyState(nextValley, r1, c1, t+1))
    }
  }
}

private data class ValleyState(val valley: List<List<Int>>, val r: Int, val c: Int, val t: Int)

private fun nextPositions(valley: List<List<Int>>, r: Int, c: Int): List<Pair<Int, Int>> {
  val next = mutableListOf<Pair<Int, Int>>()
  if (valley[r][c] == 0) next.add(Pair(r, c))
  if (r > 0 && valley[r-1][c] == 0) next.add(Pair(r-1, c))
  if (c > 0 && valley[r][c-1] == 0) next.add(Pair(r, c-1))
  if (r < valley.size - 1 && valley[r+1][c] == 0) next.add(Pair(r+1, c))
  if (c < valley.first().size-1 && valley[r][c+1] == 0) next.add(Pair(r, c+1))
  return next
}

private fun nextValley(valley: List<List<Int>>): List<List<Int>> =
  valley.mapIndexed { r, l -> List(l.size) { c -> nextCellState(valley, r, c) } }

private fun nextCellState(valley: List<List<Int>>, r: Int, c: Int): Int {
  // Assume entry and exit always stay clear.
  // Assume walls are fixed.
  if (r == 0 || r == valley.size-1 || c == 0 || c == valley[0].size - 1) return valley[r][c]
  // Moving right: ">"
  var x = 0.or(valley[r][if (c > 1) c-1 else valley[0].size - 2].and(1))
  // Moving down: "v"
  x = x.or(valley[if (r > 1) r-1 else valley.size - 2][c].and(2))
  // Moving left: "<"
  x = x.or(valley[r][if (c != valley[0].size-2) c+1 else 1].and(4))
  // Moving up: "^"
  x = x.or(valley[if (r != valley.size - 2) r+1 else 1][c].and(8))
  return x
}

//// Sometimes you gotta debug
//private fun printValley(valley: List<List<Int>>, y: Int = -1, x: Int = -1) {
//  println("=============================================")
//  println("=============================================")
//  for (r in valley.indices) {
//    for (c in valley[r].indices) {
//      if (r==y && c == x) print("E")
//      else print (when (valley[r][c]) {
//        16 -> '#'
//        8 -> '^'
//        4 -> '<'
//        2 -> 'v'
//        1 -> '>'
//        0 -> '.'
//        else -> valley[r][c].countOneBits().toString()
//      })
//    }
//    println()
//  }
//  println()
//}
