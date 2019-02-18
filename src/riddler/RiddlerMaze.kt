package riddler

import riddler.SquareTypes.ANY
import riddler.SquareTypes.DEAD
import riddler.SquareTypes.DOWN
import riddler.SquareTypes.FINISH
import riddler.SquareTypes.HORIZONTAL
import riddler.SquareTypes.LEFT
import riddler.SquareTypes.RIGHT
import riddler.SquareTypes.UP
import riddler.SquareTypes.VERTICAL
import java.io.File
import java.util.Arrays
import java.util.LinkedList

/**
 * https://fivethirtyeight.com/features/come-on-down-and-escape-the-maze/
 */
fun main(args: Array<String>) {
  // Read maze to determine what directional options are at each square.
  val squares: List<List<SquareTypes>> = readDirectionsForMaze("src/riddler/maze.txt")
  // Read maze to determine "cost" added to score at each step.
  val costs: Array<Array<Int>> = readCostsForMaze("src/riddler/maze.txt")

  // Keep track of the minimum cost of walking to each location.
  val scores: Array<Array<Int>> = Array(20) { Array(20) { 9999 } }

  // Consider each possible starting point, calculating lowest score for paths
  // from there to the finish.
  (0 until 20).forEach { i ->
    recursivelyScorePaths(LocationScore(i, 0, 0), squares, costs, scores, LinkedList())
      recursivelyScorePaths(LocationScore(0, i, 0), squares, costs, scores, LinkedList())
      recursivelyScorePaths(LocationScore(19, i, 0), squares, costs, scores, LinkedList())
      recursivelyScorePaths(LocationScore(i, 19, 0), squares, costs, scores, LinkedList())
  }

  // DEBUGGING: print lowest score seen walking to each square.
//  printScores(scores)
  squares.forEachIndexed { i, row ->
    row.forEachIndexed { j, square ->
      if (FINISH == square) {
        println("Lowest score walking to finish is: " + scores[i][j])
      }
    }
  }

}

/** Different types of squares found in a maze. */
enum class SquareTypes {
  UP,
  DOWN,
  LEFT,
  RIGHT,
  VERTICAL,
  HORIZONTAL,
  FINISH,
  DEAD,
  ANY
}

/** A tuple of (x, y) location in a maze and a score at that point. */
data class LocationScore(val x: Int, val y: Int, val score: Int) {

  fun takeStep(step: Step): LocationScore {
    return LocationScore(x + step.x, y + step.y, score)
  }

  fun addCost(cost: Int): LocationScore {
    return LocationScore(x, y, score + cost)
  }
}

/** A directional action which may be taken at a location in a maze. */
data class Step(val x: Int, val y: Int)

/**
 * Read file and parse directions for maze squares. Assumes input is comma
 * delimited.
 */
private fun readDirectionsForMaze(fileName: String): List<List<SquareTypes>> {
  return File(fileName).readLines().map { line ->
    line.split(",").map { ch: String ->
      when (ch) {
        "U"  -> UP
        "D"  -> DOWN
        "L"  -> LEFT
        "R"  -> RIGHT
        "V"  -> VERTICAL
        "H"  -> HORIZONTAL
        "X"  -> DEAD
        "E"  -> FINISH
        else -> {
          ANY
        }
      }
    }
  }
}

/**
 * Read file and parse costs associated with maze squares. Assumes input is
 * comma delimited.
 */
private fun readCostsForMaze(fileName: String): Array<Array<Int>> {
  val stepCosts: Array<Array<Int>> = Array(20) { Array(20) { 0 } }
  val squares: List<List<String>> = File(fileName).readLines().map { l -> l.split(",") }
  for (i in 0 until squares.size) {
    for (j in 0 until squares[i].size) {
      val stepCost = squares[i][j].toIntOrNull()
      when (stepCost) {
        null -> stepCosts[i][j] = 0
        else -> stepCosts[i][j] = stepCost
      }
    }
  }
  return stepCosts
}

/**
 * Walk through a maze to calculate lowest scoring path to finish.
 *
 * @param startLocationScore location before updating score and identifying next step
 * @param squares the type of square at each point in maze
 * @param costs increase applied to score for stepping on each point in maze
 * @param scores lowest score seen so far at each point in maze
 * @param locationScoreQueue queue of locations used for debugging
 */
private fun recursivelyScorePaths(startLocationScore: LocationScore,
                                  squares: List<List<SquareTypes>>,
                                  costs: Array<Array<Int>>,
                                  scores: Array<Array<Int>>,
                                  locationScoreQueue: List<LocationScore>) {
  val x = startLocationScore.x
  val y = startLocationScore.y
  // Check boundaries, exit early if out of bounds.
  if (x >= squares.size || x < 0) {
    return
  }
  if (y >= squares.size || y < 0) {
    return
  }

  // Calculate updated score and get the squares we can go for this location.
  val scoreOut = startLocationScore.score + costs[x][y]
  val currentDirection = squares[x][y]

  when (currentDirection) {
    // Terminate early at dead ends.
    DEAD   -> return
    // If at the finish, store the lower of the current path's cost and the
    // best that we've seen previously.
    FINISH -> {
      if (startLocationScore.score < scores[x][y]) {
        // DEBUGGING: print path to finish.
//        println("New best solution found: $scoreOut")
//        locationScoreQueue.forEach { l -> println(l) }
        // Update score.
        scores[x][y] = startLocationScore.score
      }
    }
    else   -> {
      // Terminate early if score is worse than the lowest seen here.
      if (scores[x][y] > scoreOut) {
        // Update the lowest score we've seen at this location.
        scores[x][y] = scoreOut

        DIRECTION_STEPS.getValue(currentDirection).forEach { step ->
          // DEBUGGING: add new location to queue.
          val updatedQueue = LinkedList(locationScoreQueue)
          updatedQueue.add(startLocationScore.addCost(costs[x][y]))
          // Take recursive step, updating start location and score.
            recursivelyScorePaths(
                startLocationScore.takeStep(step).addCost(costs[x][y]),
                squares,
                costs,
                scores,
                updatedQueue
            )
        }
      }
    }
  }
}

/** The different steps which may be taken for a direction. */
private val DIRECTION_STEPS = makeDirectionSteps()

private fun makeDirectionSteps(): Map<SquareTypes, List<Step>> {
  val up = Step(-1, 0)
  val down = Step(1, 0)
  val right = Step(0, 1)
  val left = Step(0, -1)

  val directionSteps = HashMap<SquareTypes, List<Step>>()
  directionSteps[UP] = listOf(up)
  directionSteps[DOWN] = listOf(down)
  directionSteps[LEFT] = listOf(left)
  directionSteps[RIGHT] = listOf(right)
  directionSteps[VERTICAL] = listOf(up, down)
  directionSteps[HORIZONTAL] = listOf(left, right)
  directionSteps[ANY] = listOf(up, down, right, left)

  return directionSteps
}


/**
 * Debugging function to print the best score at each square in the maze.
 *
 * <p>Normalizes scores to 9 for large values to make printing consistent.
 */
private fun printScores(scores: Array<Array<Int>>) {
  (0 until 20).forEach { i ->
    (0 until 20).forEach { j ->
      scores[i][j] = minOf(9, scores[i][j])
    }
  }
  scores.map { l -> Arrays.toString(l) }.forEach { l -> println(l) }
}