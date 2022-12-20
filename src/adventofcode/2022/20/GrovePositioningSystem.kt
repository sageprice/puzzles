package adventofcode.`2022`.`20`

import java.io.File
import kotlin.math.abs

fun main() {
  val input = File("src/adventofcode/2022/20/input.txt")
    .readLines()
    .map { it.toLong() }

  println(getCoordinatesSum(mix(input, 1)))

  println(getCoordinatesSum(mix(input.map { it * 811589153 }, 10)))
}

private fun mix(coords: List<Long>, repetitions: Int): List<Long> {
  var statefulCoords = coords.mapIndexed { idx, v -> Pair(v, idx) }.toList()
  repeat (repetitions) {
    for (idx in coords.indices) {
      val i = statefulCoords.indexOfFirst { it.second == idx }
      val (v, originalIndex) = statefulCoords[i]
      if (v == 0L) continue
      val rest = statefulCoords.subList(0, i) + statefulCoords.subList(i + 1, coords.size)
      val newIndex =
        if ((i + v) >= 0) ((i + v) % rest.size).toInt()
        else {
          var x: Long = i + v
          x += (rest.size) * (1 + abs(x / rest.size))
          (x % rest.size).toInt()
        }
      statefulCoords = if (newIndex == 0) {
        rest + Pair(v, originalIndex)
      } else {
        rest.subList(0, newIndex) + Pair(v, originalIndex) + rest.subList(newIndex, rest.size)
      }
    }
  }
  return statefulCoords.map { it.first }
}

private fun getCoordinatesSum(file: List<Long>): Long {
  val zIdx = file.indexOf(0)
  return file[(zIdx + 1000) % file.size] + file[(zIdx + 2000) % file.size] + file[(zIdx + 3000) % file.size]
}