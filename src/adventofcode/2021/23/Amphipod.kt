package adventofcode.`2021`.`23`

import java.io.File

fun main() {
  val floorplan =
    File("src/adventofcode/2021/23/input.txt")
      .readText()
      .replace("\n", "")
      .replace("_", "")
  println(floorplan)

  println("-----------")
//  getMoves(floorplan).forEach {(_, fl) ->
//    println("-----------")
//    fl.chunked(fl.length/5).forEach { println(it) } }
  // Part 1
  cache[floorplan] = 0L
  val results = explore(floorplan)
  println("Solution has min cost ${cache[p1Solution]}")
  println(cache.size)
  for (result in results!!) {
    println("-------------")
    result.chunked(result.length/5).forEach { println(it) }
  }
}

val cache = mutableMapOf<String, Long>()

val costs = mapOf(
  Pair('A', 1),
  Pair('B', 10),
  Pair('C', 100),
  Pair('D', 1000),
)

const val p1Solution = "##############...........####A#B#C#D######A#B#C#D################"
const val p2Solution = "##############...........####A#B#C#D######A#B#C#D######A#B#C#D######A#B#C#D################"
val rooms = listOf(Pair('A',3),Pair('B',5),Pair('C',7),Pair('D',9))

private fun explore(state: String, depth: Int = 0): List<String>? {
//  state.chunked(state.length/5).forEach { println("\t".repeat(depth) + it) }
  var tail: List<String>? = null
//  if (depth > 50) return null
  for ((c, s) in getMoves(state)) {
    val cost = cache[state]!! + costs[c]!!
    if (cost > cache[p1Solution] ?: 30_000) continue
    val priorCost = cache[s]
    if (priorCost == null || priorCost > cost) {
      cache[s] = cost
      if (s == p1Solution) {
        println("Found a solution with cost $cost")
        return listOf(s)
      }
      val result = explore(s, depth + 1)
      if (result != null) {
        tail = listOf(s) + result
      }
    }
  }
  return tail
}

private fun swap(state: String, i: Int, j: Int): String {
  assert(i < j)
  return state.substring(0, i) +
      state[j] +
      state.substring(i+1, j) +
      state[i] +
      state.substring(j+1)
}

private fun getMoves(state: String): List<Pair<Char, String>> {
  val moves = mutableListOf<Pair<Char, String>>()
  val rowLen = state.length / 5
  // If in our room and one off bottom, move to bottom. Always do this when possible for simplicity.
  for ((ch, roomNum) in rooms) {
    if (state[3*rowLen + roomNum] == '.' && state[2*rowLen + roomNum] == ch) {
      moves.add(Pair(ch, swap(state, 2*rowLen+roomNum, 3*rowLen+roomNum)))
      return moves
    }
  }
  // Heuristics 1+2 -- move if over a room, and only enter room when empty or occupied by a friend.
  for ((room, roomNum) in rooms) {
    val ch = state[rowLen + roomNum]
    if (ch in ".#") continue
    // An amphipod is over a room -- it must move in or move aside.
    // We're boxed in! No moves allowed
    if (state[rowLen+roomNum-1] != '.' && state[rowLen+roomNum+1] != '.') return moves
    // Amphipod is over its own room, and there is no one else (or only its friend) there! Move in time.
    return if (room == ch
      && state[2 * rowLen + roomNum] == '.'
      && (state[3*rowLen+roomNum] == ch || state[3*rowLen+roomNum] == '.')) {
      moves.add(Pair(ch, swap(state, rowLen + roomNum, 2*rowLen + roomNum)))
      moves
    } else {
      // Not our room or there is a stranger -- move immediately.
      if (state[rowLen+roomNum-1] == '.') {
        moves.add(Pair(ch, swap(state, rowLen+roomNum-1, rowLen+roomNum)))
      }
      if (state[rowLen+roomNum+1] == '.') {
        moves.add(Pair(ch, swap(state, rowLen + roomNum, rowLen + roomNum + 1)))
      }
      moves
    }
  }
  // No forced moves, so we need to check all other options
  // Moves that take an amphipod out of the wrong room.
  for ((room, roomNum) in rooms) {
    // Move intruder in bottom up to entry
    val bottom = state[3*rowLen+roomNum]
    val top = state[2*rowLen+roomNum]
    if (bottom != '.' && bottom != room && top == '.') {
      moves.add(Pair(bottom, swap(state, 2*rowLen+roomNum, 3*rowLen+roomNum)))
    }
    // Move intruder into hallway.
    if (top != '.' && (top != room || (bottom != room && bottom != '.'))) {
      moves.add(Pair(top, swap(state, rowLen+roomNum, 2*rowLen+roomNum)))
    }
  }
  for (i in rowLen until 2*rowLen) {
    val ch = state[i]
    if (ch in ".#") continue
    // We're chilling in the hall, consider moving aside
    if (state[i-1] == '.') moves.add(Pair(ch, swap(state, i-1, i)))
    if (state[i+1] == '.') moves.add(Pair(ch, swap(state, i, i+1)))
  }
  return moves
}