package adventofcode.`2021`.`23`

import java.io.File

private const val ROW_LEN = 13
private const val P1_FINAL = "#...........####A#B#C#D######A#B#C#D###"
private const val P2_FINAL = "#...........####A#B#C#D######A#B#C#D######A#B#C#D######A#B#C#D###"

private val moveCosts = mapOf(
  Pair('A', 1),
  Pair('B', 10),
  Pair('C', 100),
  Pair('D', 1000),
)
private val rooms = mapOf(Pair('A',3),Pair('B',5),Pair('C',7),Pair('D',9))

var cache = mutableMapOf<String, Long>()

fun main() {
  // We can just represent the world as a String, trimming the fat a little bit for quality of life.
  val floor1 =
    File("src/adventofcode/2021/23/input.txt")
      .readText()
      .replace("\n", "")
      .drop(ROW_LEN)
      .dropLast(ROW_LEN)

  // Part 1
//  var start = System.currentTimeMillis()
  cache[floor1] = 0L
  explore(floor1, P1_FINAL)
  println("Part 1: ${cache[P1_FINAL]}")
//  println("Time to find solution is ${System.currentTimeMillis() - start}")

  // Part 2
//  start = System.currentTimeMillis()
  val floor2 =
    File("src/adventofcode/2021/23/input2.txt")
      .readText()
      .replace("\n", "")
      .drop(ROW_LEN)
      .dropLast(ROW_LEN)
  cache = mutableMapOf(Pair(floor2, 0L)) // reset the cache, not that part 2 will ever match a part 1 state
  explore(floor2, P2_FINAL)
  println("Part 2: ${cache[P2_FINAL]}")
//  println("Time to find solution is ${System.currentTimeMillis() - start}")
}

/**
 * Object for managing amphipod movements.
 *  - {@code amphipod} the creature that moved
 *  - {@code moves} how many steps the amphipod took
 *  - {@code updatedState} world state after the amphipod moved
 */
private data class StateChange(val amphipod: Char, val moves: Int, val updatedState: String)

private fun explore(state: String, solution: String) {
  for ((c, m, s) in getChanges(state)) {
    val cost = cache[state]!! + m * moveCosts[c]!!
    if (cost > cache[solution] ?: 100_000) continue // I don't think this even matters
    val priorCost = cache[s]
    if (priorCost == null || priorCost > cost) {
      cache[s] = cost
      if (s == solution) return // nothing else to do when we hit on a solution
      explore(s, solution)
    }
  }
}

/**
 * Returns possible state changes from the input state. Possible amphipod moves may be excluded when there is a higher
 * priority action available. Implements all 3 heuristics defined in https://adventofcode.com/2021/day/23 with an
 * additional heuristic to smooth handling of amphipods currently in a room.
 */
private fun getChanges(state: String): List<StateChange> {
  val moves = mutableListOf<StateChange>()
  val roomDepth = -1 + state.length / ROW_LEN
  // Made up heuristics for simplifying room behavior:
  //  1. If an amphipod is in the right room and can move down, always move down.
  //  2. If there is an amphipod in the wrong room, move it or anything blocking it up toward the exit.
  for ((ch, roomNum) in rooms) {
    for (depth in roomDepth downTo 2) {
      val bottom = depth * ROW_LEN + roomNum
      val top = bottom - ROW_LEN
      // Good amphipod above, push it down.
      if (state[bottom] == '.' && state[top] == ch && (depth+1..roomDepth).all {
            d -> state[d*ROW_LEN + roomNum] == ch || state[d*ROW_LEN + roomNum] == '.' }) {
        moves.add(StateChange(ch, 1, swap(state, top, bottom)))
        return moves
      }
      // Space above and an amphipod in the wrong room here or below, so push this up.
      if (
        state[bottom] != '.' && // bottom is an amphipod
        state[top] == '.' && // top is empty
        (depth..roomDepth).any { d -> state[d*ROW_LEN + roomNum] != ch && state[d*ROW_LEN + roomNum] != '.' }) {
        moves.add(StateChange(state[bottom], 1, swap(state, top, bottom)))
        return moves
      }
    }
  }

  // Heuristics 1+2 -- move if over a room, and only enter room when empty or occupied by a friend.
  for ((room, roomNum) in rooms) {
    val ch = state[roomNum]
    if (ch in ".#") continue
    // An amphipod is over a room -- it must move in or move aside.
    // Amphipod is over its own room, and there is no one else (or only its friends) there! Time to move on in.
    return if (room == ch
      && state[ROW_LEN + roomNum] == '.'
      && (2..roomDepth).all { d -> state[d*ROW_LEN+roomNum] == ch || state[d*ROW_LEN+roomNum] == '.' }) {
      moves.add(StateChange(ch, 1, swap(state, roomNum, ROW_LEN + roomNum)))
      moves
    } else {
      // Not our room or there is a stranger -- move immediately.
      if (state[roomNum-1] == '.') {
        moves.add(StateChange(ch, 1, swap(state, roomNum-1, roomNum)))
      }
      if (state[roomNum+1] == '.') {
        moves.add(StateChange(ch, 1, swap(state, roomNum, roomNum + 1)))
      }
      moves
    }
  }
  // Heuristic 3 consequence: any amphipod that can move into its room should always do so, when there are no intruders.
  for (i in 1 until ROW_LEN-1) { // skip first and last
    val ch = state[i]
    if (ch == '.') continue
    val room = rooms[ch] ?: error("Could not find room for $ch")
    // Since we prioritize moving amphipods up if they should step out,
    // any time a room's entry is empty the room must be clear of intruders.
    if (state[room+ROW_LEN] == '.') {
      // We can only move into our room if there are no amphipods between us and the room.
      if (room < i && (room until i).all { r -> state[r] == '.' }) {
        moves.add(StateChange(ch, 1 + i - room, swap(state, i, room+ROW_LEN)))
        return moves
      }
      if (room > i && (room downTo i+1).all { state[it] == '.' }) {
        moves.add(StateChange(ch, 1 + room - i, swap(state, i, room+ROW_LEN)))
        return moves
      }
    }
  }
  // No forced moves, so we consider all remaining moves. Since everything inside a room has moved and things in the
  // hallway are locked, this means moving an amphipod out of a room to wait somewhere until its room opens.
  for ((ch, roomNum) in rooms) {
    val top = state[ROW_LEN+roomNum]
    // Only move into hallway when there is an intruder somewhere in the room.
    if ((1..roomDepth).any { d -> state[d*ROW_LEN+roomNum] != '.' && state[d*ROW_LEN+roomNum] != ch }) {
      // Check for open waiting spots (not above a room) to our right.
      for (r in (roomNum+1) until ROW_LEN) {
        if (state[r] != '.') break // ran into another amphipod
        if (state[ROW_LEN+r] == '#') { // check we're in a gap between rooms or a cubby
          moves.add(StateChange(top, 1 + r - roomNum, swap(state, r, ROW_LEN+roomNum)))
        }
      }
      // Check for open waiting spots (not above a room) to our left.
      for (r in (roomNum-1) downTo 1) {
        if (state[r] != '.') break // ran into another amphipod
        if (state[ROW_LEN+r] == '#') { // check we're in a gap between rooms or a cubby
          moves.add(StateChange(top, 1 + roomNum - r, swap(state, r, ROW_LEN+roomNum)))
        }
      }
    }
  }
  return moves
}

/**
 * Returns a copy of {@code state} with characters at indices {@code i} and {@code j} swapped.
 */
private fun swap(state: String, i: Int, j: Int): String {
  assert(i < j)
  val chars = state.toCharArray()
  val temp = chars[i]
  chars[i] = chars[j]
  chars[j] = temp
  // Calling String(CharArray) is about 1.5x faster than concatenating substrings, and ~2.25x
  // faster than using joinToString (over the full runtime of the current approach). idk y
  return String(chars)
}