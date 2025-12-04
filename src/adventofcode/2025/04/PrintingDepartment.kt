package adventofcode.`2025`.`04`

import java.io.File

/** https://adventofcode.com/2025/day/4 */
fun main() {
  val data = File("src/adventofcode/2025/04/input.txt").readLines().map { it.toCharArray() }

  println(getRollsWithLtXNeighbors(data, 4).size)
  println(removeAsManyAsPossible(data))
}

private fun getRollsWithLtXNeighbors(data: List<CharArray>, x: Int): List<Pair<Int, Int>> {
  val points = mutableListOf<Pair<Int, Int>>()
  for (r in data.indices) for (c in data[r].indices) {
    if (data[r][c] != '@') continue
    var neighborCount = 0
    for (i in -1..1) for (j in -1..1) {
      if (i == 0 && j == 0) continue
      if (r+i in data.indices && c+j in data[r].indices) {
        if (data[r+i][c+j] == '@') neighborCount++
      }
    }
    if (neighborCount < x) points.add(r to c)
  }
  return points
}

private fun removeAsManyAsPossible(data: List<CharArray>): Int {
  var removedCount = 0
  while (true) {
    val toRemove = getRollsWithLtXNeighbors(data, 4)
    if (toRemove.isEmpty()) return removedCount
    removedCount += toRemove.size
    toRemove.forEach { (r, c) ->
      data[r][c] = '.'
    }
  }
}