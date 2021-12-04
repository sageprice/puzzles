package adventofcode.`2021`.`04`

import java.io.File

fun main() {
  val bingo: List<String> =
    File("src/adventofcode/2021/04/input.txt")
      .readLines()
  val drawn = bingo.first().split(",").map { it.toInt() }

  val boardLines = bingo.subList(2, bingo.size).filter { it.isNotEmpty() }
  val boards: List<List<List<Int>>> = boardLines.chunked(5)
    .map { b ->
      b.map { row ->
        row.split(" ")
          .filter { l -> l != "" }
          .map { i -> i.toInt() } }
    }

  val drawnSet = mutableSetOf<Int>()
  var latest = 0
  var winningBoard: List<List<Int>>? = null
  for (n in drawn) {
    drawnSet.add(n)
    latest = n
    for (board in boards) {
      if (isWinner(board, drawnSet)) {
        winningBoard = board
        break
      }
    }
    if (winningBoard != null) break
  }
  println(winningBoard!!.flatten().filter { !drawnSet.contains(it) }.sum() * latest)

  // Part 2
  latest = 0
  var losingBoards: MutableList<List<List<Int>>> = boards.toMutableList()
  var lastBoard: List<List<Int>>? = null
  for (n in drawn) {
    drawnSet.add(n)
    latest = n
    losingBoards = losingBoards.filter { !isWinner(it, drawnSet) }.toMutableList()
    if (losingBoards.size == 1) lastBoard = losingBoards.first()
    if (losingBoards.isEmpty()) break
  }

  println(lastBoard!!.flatten().filter { !drawnSet.contains(it) }.sum() * latest)
}

private fun isWinner(board: List<List<Int>>, nums: Set<Int>): Boolean {
  for (n in nums) {
    for (r in board) if (nums.containsAll(r)) return true
    for (i in board.indices) if (nums.containsAll(board.map { it[i] })) return true
  }
  return false
}