package adventofcode.`2024`.`15`

import java.io.File

/** https://adventofcode.com/2024/day/15 */
fun main() {
  val (wh, moves) = File("src/adventofcode/2024/15/input.txt").readText().split("\n\n")

  // Part 1
  var warehouse = parseWarehouse(wh)
  val actions = moves.replace("\n", "")
  actions.forEach { action ->
    warehouse = warehouse.moveRobot(action)
  }
  println(warehouse.getGpsTotal())

  // Part 2
  var bigWarehouse = parseBigWarehouse(expand(wh))
  actions.forEach { action ->
    bigWarehouse = bigWarehouse.moveRobot(action)
  }
  println(bigWarehouse.getGpsTotal())
}

private fun expand(str: String): String {
  return str.map { c ->
    when (c) {
      '#' -> "##"
      '@' -> "@."
      'O' -> "[]"
      '.' -> ".."
      else -> c
    }
  }.joinToString("")
}

private fun parseBigWarehouse(str: String): BigWarehouse {
  val lines = str.split("\n")
  var robot = -1 to -1
  val boxes = mutableMapOf<Coord, Pair<Coord, Coord>>()
  val walls = mutableSetOf<Coord>()
  for (r in lines.indices) {
    for (c in lines[r].indices) {
      if (lines[r][c] == '#') {
        walls.add(r to c)
      } else if (lines[r][c] == '[') {
        boxes[r to c] = (r to c) to (r to c+1)
        boxes[r to c+1] = (r to c) to (r to c+1)
      } else if (lines[r][c] == '@') {
        robot = r to c
      }
    }
  }
  return BigWarehouse(boxes, walls, robot, lines.first().length * 2, lines.size)
}

private data class BigWarehouse(
  val boxes: MutableMap<Coord, Pair<Coord, Coord>>,
  val walls: MutableSet<Coord>,
  val robot: Coord,
  val width: Int,
  val height: Int
) {

  /** Prints an ASCII diagram of warehouse state. Helpful for debugging. */
  fun print() {
    for (r in 0 until height) {
      for (c in 0 until width) {
        if (r to c in boxes) {
          print(if (r to c == boxes[r to c]?.first) "[" else "]" )
        } else if (r to c in walls) {
          print("#")
        } else if (r to c == robot) {
          print("@")
        } else {
          print(" ")
        }
      }
      println()
    }
  }

  fun getGpsTotal(): Long =
    boxes.filter { (k, v) -> k == v.first }.keys.sumOf { (a, b) -> 100L * a + b }

  fun moveRobot(action: Char): BigWarehouse {
    val deltas = getDeltas(action)
    val rNext = robot + deltas
    if (rNext in walls) return this
    if (rNext in boxes) {
      val bNext: Map<Pair<Coord, Coord>, Pair<Coord, Coord>> = getMovedBoxes(rNext, deltas) ?: return this
      bNext.keys.forEach { before ->
        boxes.remove(before.first)
        boxes.remove(before.second)
      }
      bNext.values.forEach { after ->
        boxes[after.first] = after.first to after.second
        boxes[after.second] = after.first to after.second
      }
    }
    return BigWarehouse(boxes, walls, rNext, width, height)
  }

  private fun getMovedBoxes(start: Coord, deltas: Pair<Int, Int>): Map<Pair<Coord, Coord>, Pair<Coord, Coord>>? {
    val movedBoxes = mutableMapOf<Pair<Coord, Coord>, Pair<Coord, Coord>>()
    if (deltas.first == 0) {
      var boxToMove = start
      while (boxToMove in boxes) {
        val (l, r) = boxes[boxToMove]!!
        val nl = l + deltas
        val nr = r + deltas
        if (nl in walls || nr in walls) return null
        movedBoxes[l to r] = nl to nr
        boxToMove = boxToMove + deltas + deltas
      }
      return movedBoxes
    } else {
      var boxesToMove = mutableListOf(start)
      while (boxesToMove.isNotEmpty()) {
        val actualBoxes = boxesToMove.filter { it in boxes }
        boxesToMove = mutableListOf()
        if (actualBoxes.isEmpty()) return movedBoxes
        for (box in actualBoxes) {
          val (l, r) = boxes[box] ?: throw IllegalStateException("Could not find box $box")
          val nl = l + deltas
          val nr = r + deltas
          if (nl in walls || nr in walls) return null
          movedBoxes[l to r] = nl to nr
          boxesToMove.add(nl)
          boxesToMove.add(nr)
        }
      }
      return movedBoxes
    }
  }
}

private fun parseWarehouse(str: String): Warehouse {
  val lines = str.split("\n")
  var robot = -1 to -1
  val boxes = mutableSetOf<Coord>()
  val walls = mutableSetOf<Coord>()
  for (r in lines.indices) {
    for (c in lines[r].indices) {
      if (lines[r][c] == '#') {
        walls.add(r to c)
      } else if (lines[r][c] == 'O') {
        boxes.add(r to c)
      } else if (lines[r][c] == '@') {
        robot = r to c
      }
    }
  }
  return Warehouse(boxes, walls, robot, lines.first().length, lines.size)
}

private data class Warehouse(
  val boxes: MutableSet<Coord>,
  val walls: MutableSet<Coord>,
  var robot: Coord,
  val width: Int,
  val height: Int
) {

  fun print() {
    for (r in 0 until height) {
      for (c in 0 until width) {
        if (r to c in boxes) print("O")
        else if (r to c in walls) print("#")
        else if (r to c == robot) print("@")
        else print(" ")
      }
      println()
    }
  }

  fun getGpsTotal(): Long = boxes.sumOf { 100L*it.first + it.second }

  fun moveRobot(action: Char): Warehouse {
    val deltas = getDeltas(action)
    val rNext = robot + deltas
    if (rNext in walls) return this
    if (rNext in boxes) {
      val bNext: Coord = getBoxNext(rNext, deltas) ?: return this
      boxes.remove(rNext)
      boxes.add(bNext)
      this.robot = rNext
    }
    return Warehouse(boxes, walls, rNext, width, height)
  }

  private fun getBoxNext(start: Coord, deltas: Pair<Int, Int>): Coord? {
    var newBox = start
    while (newBox in boxes) {
      newBox += deltas
      if (newBox in walls) return null
    }
    return newBox
  }
}

private fun getDeltas(action: Char): Pair<Int, Int> {
  return when (action) {
    '^' -> -1 to 0
    '>' -> 0 to 1
    'v' -> 1 to 0
    '<' -> 0 to -1
    else -> throw IllegalArgumentException("Unrecognized move: \"$action\"")
  }
}

private typealias Coord = Pair<Int, Int>
private infix operator fun Coord.plus(other: Coord): Coord {
  return this.first + other.first to this.second + other.second
}

