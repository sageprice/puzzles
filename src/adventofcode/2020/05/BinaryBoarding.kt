package adventofcode.`2020`.`05`

import java.io.File

fun main() {
  val boardingPasses: List<BoardingPass> =
    File("src/adventofcode/2020/05/input.txt")
      .readLines()
      .map { extractPass(it) }

  // Part 1
  println(boardingPasses.map { it.getSeatId() }.max())

  // Part 2
  val sortedIds = boardingPasses.map { it.getSeatId() }.sorted()
  for (i in sortedIds.indices) {
    if (sortedIds[i] != sortedIds[i+1] - 1) {
      println(sortedIds[i] + 1)
      break
    }
  }
}

private fun extractPass(bp: String): BoardingPass {
  var zone = 0
  var seat = 0
  for (c in bp) {
    when (c) {
      'F' -> zone = 2*zone
      'B' -> zone = 2*zone + 1
      'L' -> seat = 2*seat
      'R' -> seat = 2*seat + 1
    }
  }
  return BoardingPass(zone, seat)
}

private data class BoardingPass(val zone: Int, val seat: Int)

private fun BoardingPass.getSeatId(): Int {
  return zone * 8 + seat
}