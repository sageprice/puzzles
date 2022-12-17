package adventofcode.`2022`.`17`

import java.io.File
import kotlin.math.max

fun main() {
  val input = File("src/adventofcode/2022/17/input.txt").readText()

  var cave = mutableSetOf<Pair<Long, Long>>()
  val h1 = simulateRockFall(cave, input, 2022)
  println(h1)

  cave = mutableSetOf()
  val h2 = simulateRockFall(cave, input, 1000000000000L)
  println(h2)
}

private data class SimState(val cave: String, val stepIndex: Long)

private fun simulateRockFall(
  cave: MutableSet<Pair<Long, Long>>,
  pushes: String,
  rockCount: Long
): Long {
  // Since part 2 is so large, we need to skip most of the simulation. We do this by detecting a cycle. For my input,
  // there just happened to be a point where we get a full horizontal line. I defined this to be the "clear" point and
  // pretend everything below it is wiped in my initial attempt, then later realized this was a convenient place to
  // check for cycles. Hence, the cache. HOWEVER: this code will not work if an input does not generate a full
  // horizontal line, like the example input. A more robust solution would check the top N lines, instruction number,
  // and most recently dropped rock to verify the cycle.
  val clearanceCache = mutableMapOf<SimState, Pair<Long, Long>>()

  val right = 8L
  var startHeight = 4L
  for (i in 0..right) cave.add(Pair(0, i))
  var stepIndex = 0L
  var goDown = false
  var i = 1L
  var hasSkipped = false
  var additionalHeight = 0L
  while (i <= rockCount) {
    var rock = spawnRock(i, startHeight, 3)
    var rockIsFalling = true
    while (rockIsFalling) {
      val movement = if (goDown) 'v' else pushes[(stepIndex++ % pushes.length).toInt()]
      when (val result = moveRock(movement, rock, cave, right)) {
        Impeded -> {}
        is Moved -> rock = result.rock
        Stopped -> {
          rockIsFalling = false
          cave.addAll(rock)
          for (h in rock.map { it.first }.distinct()) {
            if ((1 until right).all { cave.contains(Pair(h, it)) }) {
              // We can drop everything below the line since rocks won't go below it
              cave.removeAll(cave.filter { it.first < h }.toSet())
              // Cycle detection and skipping code
              if (!hasSkipped) {
                val state = SimState(caveString(cave, startHeight, right), stepIndex % pushes.length)
                val prior = clearanceCache[state]
                if (prior != null) {
                  val (priorRock, priorHeight) = prior
                  val rocksSincePrior = i - priorRock
                  val futureReps = (rockCount - i) / rocksSincePrior
                  i += rocksSincePrior * futureReps
                  additionalHeight = (startHeight - priorHeight) * futureReps
                  hasSkipped = true
                }
                clearanceCache[state] = Pair(i, startHeight)
              }
            }
          }
          startHeight = max(startHeight, 4 + rock.maxOf { it.first })
        }
      }
      goDown = !goDown
    }
    i++
  }
  return additionalHeight + startHeight - 4
}

private fun printCave(cave: Set<Pair<Long, Long>>, height: Long, width: Long) {
  println("=========================")
  println(caveString(cave, height, width))
  for (i in 0..width+1) print("-")
  println()
}

private fun caveString(cave: Set<Pair<Long, Long>>, height: Long, width: Long): String {
  var s = ""
  for (h in height downTo 1) {
    if (h < height-4 && (1 until width).none { cave.contains(Pair(h, it)) }) break
    s += "|"
    for (x in 1 until width) s += if (cave.contains(Pair(h, x))) "█" else '.'
    s += "|\n"
  }
  return s
}

private fun spawnRock(i: Long, startHeight: Long, startLeft: Long): List<Pair<Long, Long>> {
  return when ((i % 5).toInt()) {
    1 -> listOf(
      Pair(startHeight, startLeft),
      Pair(startHeight, startLeft+1),
      Pair(startHeight, startLeft+2),
      Pair(startHeight, startLeft+3))
    2 -> listOf(
      Pair(startHeight+1, startLeft),
      Pair(startHeight, startLeft+1),
      Pair(startHeight+1, startLeft+2),
      Pair(startHeight+1, startLeft+1),
      Pair(startHeight+2, startLeft+1),)
    3 -> listOf(
      Pair(startHeight, startLeft),
      Pair(startHeight, startLeft+1),
      Pair(startHeight, startLeft+2),
      Pair(startHeight+1, startLeft+2),
      Pair(startHeight+2, startLeft+2))
    4 -> listOf(
      Pair(startHeight, startLeft),
      Pair(startHeight+1, startLeft),
      Pair(startHeight+2, startLeft),
      Pair(startHeight+3, startLeft))
    else -> listOf(
      Pair(startHeight, startLeft),
      Pair(startHeight, startLeft+1),
      Pair(startHeight+1, startLeft),
      Pair(startHeight+1, startLeft+1))
  }
}

private sealed class MoveResult
private object Impeded: MoveResult()
private object Stopped: MoveResult()
private data class Moved(val rock: List<Pair<Long, Long>>): MoveResult()

private fun moveRock(
  movement: Char, rock: List<Pair<Long, Long>>, cave: Set<Pair<Long, Long>>, rightWall: Long): MoveResult {
  var dh = 0
  var dx = 0
  when (movement) {
    '<' -> dx = -1
    '>' -> dx = +1
    'v' -> dh = -1
  }
  val movedRock = rock.map { (h, x) -> Pair(h + dh, x + dx) }
  if (movedRock.any { it in cave } || movedRock.any{ (_, x) -> x <= 0 || x >= rightWall }) {
    if (movement == 'v') return Stopped
    return Impeded
  }
  return Moved(movedRock)
}