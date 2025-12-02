package adventofcode.`2025`.`01`

import java.io.File

/** https://adventofcode.com/2025/day/1 */
fun main() {
  val rotations = File("src/adventofcode/2025/01/input.txt").readLines()
    .map { l ->
      val dir = l.take(1)
      val d = l.substring(1).toInt()
      if (dir == "L") {
        Left(d)
      } else {
        Right(d)
      }
    }
  println(countZeroHits(rotations))
  println(countZeroClicks(rotations))
}

private fun countZeroHits(rotations: List<Rotation>): Int {
  var p = 50
  var zeros = 0
  rotations.forEach { rot ->
    p = when (rot) {
      is Left -> (p + 100 - rot.d) % 100
      is Right -> (p + rot.d) % 100
    }
    if (p == 0) {
      zeros += 1
    }
  }
  return zeros
}

private fun countZeroClicks(rotations: List<Rotation>): Int {
  var p = 50
  var zeros = 0
  rotations.forEach { rot ->
    p = when (rot) {
      is Left -> {
        val d = rot.d % 100
        zeros += (rot.d / 100)
        if (p != 0 && p <= d) {
          zeros += 1
        }
        (p + 100 - d) % 100
      }
      is Right -> {
        val d = rot.d % 100
        zeros += (rot.d / 100)
        if (p != 0 && p + d >= 100) {
          zeros += 1
        }
        (p + d) % 100
      }
    }
  }
  return zeros
}

private sealed class Rotation
private data class Left(val d: Int): Rotation()
private data class Right(val d: Int): Rotation()
