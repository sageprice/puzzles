package adventofcode.`2021`.`23`

import java.io.File

fun main() {
  val floorplan =
    File("src/adventofcode/2021/23/input.txt")
      .readText()
      .replace("\n", "")
      .drop(rowLen)
      .dropLast(rowLen)

  // Part 1
  cache[floorplan] = 0L
  var start = System.currentTimeMillis()
  val results = explore(floorplan, p1Solution)
  println("Time to find solution is ${System.currentTimeMillis() - start}")
  println("Solution has min cost ${cache[p1Solution]}")
//  println("Cache size is ${cache.size}")
//  for (result in results!!) {
//    println("-------------")
//    result.chunked(rowLen).forEach { println(it) }
//  }

  // Part 2
  println("==================================")

//  for ((c, m) in getMoves("#.......C.B.####B#C#B#D######D#.#A#A######D#B#.#C######A#D#C#A###")) {
//    println("==============================")
//    m.chunked(rowLen).forEach { println(it) }
//  }
  println("==================================")
  println("Starting part 2")
  start = System.currentTimeMillis()
  val floor2 =
    File("src/adventofcode/2021/23/input2.txt")
      .readText()
      .replace("\n", "")
      .drop(rowLen)
      .dropLast(rowLen)
  cache = mutableMapOf(Pair(floor2, 0L))
  val r2 = explore(floor2, p2Solution)
  println("Time to find solution is ${System.currentTimeMillis() - start}")
  println("Solution has min cost ${cache[p2Solution]}")
  println("Cache size is ${cache.size}")
  r2!!.forEachIndexed { i, r ->
    println("----------------------------------")
    println("Step $i: ${cache[r]}")
    r.chunked(rowLen).forEach { c -> println(c) }
  }
}

var cache = mutableMapOf<String, Long>()

val costs = mapOf(
  Pair('A', 1),
  Pair('B', 10),
  Pair('C', 100),
  Pair('D', 1000),
)

const val rowLen = 13
const val p1Solution = "#...........####A#B#C#D######A#B#C#D###"
const val p2Solution = "#...........####A#B#C#D######A#B#C#D######A#B#C#D######A#B#C#D###"
val rooms = mapOf(Pair('A',3),Pair('B',5),Pair('C',7),Pair('D',9))

private fun explore(state: String, solution: String, depth: Int = 0): List<String>? {
//  state.chunked(state.length/(state.length / rowLen)).forEach { println("\t".repeat(depth) + it) }
//  if (depth > 2) return null
  var tail: List<String>? = null
  for ((c, s) in getMoves(state)) {
    val cost = (cache[state] ?: error("Could not find state $state")) + (costs[c] ?: error("Could not find cost $c"))
    if (cost > cache[solution] ?: 70_000) continue
    val priorCost = cache[s]
    if (priorCost == null || priorCost > cost) {
      cache[s] = cost
      if (s == solution) {
//        println("Found a solution with cost $cost")
        return listOf(s)
      }
      val result = explore(s, solution, depth + 1)
      if (result != null) {
        tail = listOf(s) + result
      }
    }
  }
  return tail
}

private fun swap(state: String, i: Int, j: Int): String {
  assert(i < j)
  val chars = state.toCharArray()
  val temp = chars[i]
  chars[i] = chars[j]
  chars[j] = temp
  return String(chars) // Takes 36.725 seconds
  //.joinToString(separator = "") // Takes 84.944 seconds with joinToString
//  return state.substring(0, i) + // Takes 50.210 seconds
//      state[j] +
//      state.substring(i+1, j) +
//      state[i] +
//      state.substring(j+1)
}

private fun getMoves(state: String): List<Pair<Char, String>> {
  val moves = mutableListOf<Pair<Char, String>>()
  val roomDepth = -1 + state.length / rowLen
  // Made up in-room heuristics for simplification.
  for ((ch, roomNum) in rooms) {
    for (depth in roomDepth downTo 2) {
      val bottom = depth * rowLen + roomNum
      val top = bottom - rowLen
      // Good amphipod above, push it down.
      if (state[bottom] == '.' && state[top] == ch && (depth+1..roomDepth).all {
            d -> state[d*rowLen + roomNum] == ch || state[d*rowLen + roomNum] == '.' }) {
        moves.add(Pair(ch, swap(state, top, bottom)))
        return moves
      }
      // Space above, push up.
      if (
        state[bottom] != '.' && // bottom is an amphipod
        state[top] == '.' && // top is empty
        (depth..roomDepth).any { d -> state[d*rowLen + roomNum] != ch && state[d*rowLen + roomNum] != '.' }) {
        moves.add(Pair(state[bottom], swap(state, top, bottom)))
        return moves
      }
    }
  }

  // Heuristics 1+2 -- move if over a room, and only enter room when empty or occupied by a friend.
  for ((room, roomNum) in rooms) {
    val ch = state[roomNum]
    if (ch in ".#") continue
    // An amphipod is over a room -- it must move in or move aside.
    // We're boxed in! No moves allowed
    if (state[roomNum-1] != '.' && state[roomNum+1] != '.') return moves
    // Amphipod is over its own room, and there is no one else (or only its friends) there! Time to move on in.
    return if (room == ch
      && state[rowLen + roomNum] == '.'
      && (2..roomDepth).all { d -> state[d*rowLen+roomNum] == ch || state[d*rowLen+roomNum] == '.' }) {
      moves.add(Pair(ch, swap(state, roomNum, rowLen + roomNum)))
      moves
    } else {
      // Not our room or there is a stranger -- move immediately.
      if (state[roomNum-1] == '.') {
        moves.add(Pair(ch, swap(state, roomNum-1, roomNum)))
      }
      if (state[roomNum+1] == '.') {
        moves.add(Pair(ch, swap(state, roomNum, roomNum + 1)))
      }
      moves
    }
  }
  // No forced moves, so we need to check all other options
  // Moves that take an amphipod out of the wrong room.
  for ((ch, roomNum) in rooms) {
    val top = state[rowLen+roomNum]
    // Move intruder into hallway.
    if (top != '.' && (1..roomDepth).any { d -> state[d*rowLen+roomNum] !in ".$ch" }) {
      moves.add(Pair(top, swap(state, roomNum, rowLen+roomNum)))
    }
  }
  // Shuffling amphipods in the hall sideways.
  for (i in 1 until rowLen) {
    val ch = state[i]
    if (ch in ".#") continue
    // We're chilling in the hall, consider moving aside
    if (state[i-1] == '.') moves.add(Pair(ch, swap(state, i-1, i)))
    if (state[i+1] == '.') moves.add(Pair(ch, swap(state, i, i+1)))
  }
  return moves
}