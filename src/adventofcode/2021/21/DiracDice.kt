package adventofcode.`2021`.`21`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val (p1Start, p2Start) = File("src/adventofcode/2021/21/input.txt")
    .readLines()
    .map { it.split(" ").last().toInt() }

  // Part 1
  val (p1Score, p2Score, totalRolls) = playDeterministicGame(p1Start, p2Start)
  println(totalRolls * min(p1Score, p2Score))

  // Part 2
  val (p1Wins, p2Wins) = playDiracGame(p1Start, p2Start)
  print(max(p1Wins, p2Wins))
}

private data class Player(
  val position: Int,
  val score: Int
)

private data class GameState(
  val p1: Player,
  val p2: Player,
  val p1Turn: Boolean
)

private fun playDiracGame(
  p1Start: Int,
  p2Start: Int
): Pair<Long, Long> {
  val states = mutableMapOf(
    Pair(GameState(
      p1 = Player(p1Start, 0), p2 = Player(p2Start, 0), p1Turn = true
    ), 1L)
  )
  var p1Wins = 0L
  var p2Wins = 0L
  val rollDistribution = listOf<Pair<Int, Long>>(
    Pair(3, 1),
    Pair(4, 3),
    Pair(5, 6),
    Pair(6, 7),
    Pair(7, 6),
    Pair(8, 3),
    Pair(9, 1)
  )
  while (states.isNotEmpty()) {
    val lowestScoringGame = states.keys.minByOrNull { (p1, p2, _) -> p1.score + p2.score }!!
    val ways = states.remove(lowestScoringGame)!!
    val (p1, p2, p1Turn) = lowestScoringGame
    if (p1Turn) {
      rollDistribution.forEach { (roll, count) ->
        var pos = (p1.position + roll) % 10
        if (pos == 0) pos = 10
        val score = p1.score + pos
        if (score >= 21) {
          p1Wins += ways * count
        } else {
          val newState = GameState(p1 = Player(pos, score), p2 = p2, false)
          if (states.containsKey(newState)) {
            states[newState] = states[newState]!! + ways * count
          } else {
            states[newState] = ways * count
          }
        }
      }
    } else {
      rollDistribution.forEach { (roll, count) ->
        var pos = (p2.position + roll) % 10
        if (pos == 0) pos = 10
        val score = p2.score + pos
        if (score >= 21) {
          p2Wins += ways * count
        } else {
          val newState = GameState(p1 = p1, p2 = Player(pos, score), true)
          if (states.containsKey(newState)) {
            states[newState] = states[newState]!! + ways * count
          } else {
            states[newState] = ways * count
           }
        }
      }
    }
  }
  return Pair(p1Wins, p2Wins)
}

private fun playDeterministicGame(
  p1Start: Int,
  p2Start: Int
): Triple<Int, Int, Int> {
  var p1 = p1Start
  var p2 = p2Start
  var p1Score = 0
  var p2Score = 0
  var totalRolls = 0
  var roll = 1
  var p1Turn = true
  while (p1Score < 1000 && p2Score < 1000) {
    val move = (1..3).sumBy {
      if (roll > 100) roll -= 100
      totalRolls++
      roll++
    }
    if (p1Turn) {
      p1 = (p1 + move) % 10
      if (p1 == 0) p1 = 10
      p1Score += p1
    } else {
      p2 = (p2 + move) % 10
      if (p2 == 0) p2 = 10
      p2Score += p2
    }
    p1Turn = !p1Turn
  }
  return Triple(p1Score, p2Score, totalRolls)
}