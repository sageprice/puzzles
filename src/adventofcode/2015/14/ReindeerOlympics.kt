package adventofcode.`2015`.`14`

import java.io.File

fun main() {
  val reindeers = File("src/adventofcode/2015/14/input.txt").readLines().map { parseReindeer(it) }
  println(reindeers.maxOf { getFlightDistance(it, 2503) })
  println(trackWinners(reindeers, 2503))
}

private val REINDEER_PATTERN =
  Regex("(?<reindeer>[A-Za-z]+) can fly (?<flightSpeed>\\d+) km/s for (?<flightSecs>\\d+) seconds, but then must rest for (?<restSecs>\\d+) seconds.")

private fun parseReindeer(line: String): Reindeer {
  val groups = REINDEER_PATTERN.matchEntire(line)!!.groupValues
  return Reindeer(
    name = groups[1],
    speed = groups[2].toLong(),
    time = groups[3].toInt(),
    rest = groups[4].toInt()
  )
}

private fun getFlightDistance(reindeer: Reindeer, totalTime: Int): Long {
  val cycleTime = reindeer.time + reindeer.rest
  val flights = totalTime / cycleTime
  var d = reindeer.speed * reindeer.time * flights
  val remainingTime = totalTime - flights * cycleTime
  d += if (remainingTime >= reindeer.time) {
    reindeer.speed * reindeer.time
  } else {
    reindeer.speed * remainingTime
  }
  return d
}

private fun trackWinners(reindeers: List<Reindeer>, totalTime: Int): Long {
  val distances = reindeers.associateWith { 0L }.toMutableMap()
  val winners = reindeers.associateWith { 0L }.toMutableMap()
  for (i in 0 .. totalTime) {
    reindeers.forEach { rd ->
      val plusD = if (i % (rd.rest + rd.time) < rd.time) rd.speed else 0
      distances[rd] = (distances[rd] ?: 0) + plusD
    }
    val roundWinner = reindeers.maxBy { distances[it]!! }
    winners[roundWinner] = 1 + (winners[roundWinner] ?: 0)
  }
  return winners.maxOf { it.value }
}

private data class Reindeer(
  val name: String,
  val speed: Long,
  val time: Int,
  val rest: Int
)