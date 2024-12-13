package adventofcode.`2024`.`13`

import java.io.File

/** https://adventofcode.com/2024/day/13 */
fun main() {
  val clawMachines = File("src/adventofcode/2024/13/input.txt")
    .readText()
    .split("\n\n")
    .map { parseClawMachine(it) }

  println(
    clawMachines.mapNotNull { findSolution(it) }
      .filter { (a, b) -> a <= 100 && b <= 100 }
      .sumOf { (a, b) -> 3*a + b })
  println(
    clawMachines.mapNotNull { findSolution(it, delta = 10_000_000_000_000L) }
      .sumOf { (a, b) -> 3*a + b })
}

private fun findSolution(clawMachine: ClawMachine, delta: Long = 0L): Pair<Long, Long>? {
  val (ax, ay, bx, by, px0, py0) = clawMachine
  if (ax * by == bx * ay) {
    throw IllegalArgumentException("Claw machine may have multiple solutions, time to be smart: $clawMachine")
  }
  val px = px0 + delta
  val py = py0 + delta
  // We do a little algebra
  val b = (ay*px - ax*py) / (ay*bx - ax*by)
  val a = (px - b*bx) / ax
  // Check both equations in case rounding leads to a solution for just one of them.
  return if (a*ax + b*bx == px && a*ay + b*by == py) a to b else null
}

private fun parseClawMachine(str: String): ClawMachine {
  val (a, b, p) = str.split("\n")
  val (ax, ay) = a.split(": ").last().split(", ").map { it.split("+").last().toLong() }
  val (bx, by) = b.split(": ").last().split(", ").map { it.split("+").last().toLong() }
  val (px, py) = p.split(": ").last().split(", ").map { it.split("=").last().toLong() }
  return ClawMachine(ax, ay, bx, by, px, py)
}

private data class ClawMachine(
  val ax: Long, val ay: Long, val bx: Long, val by: Long, val px: Long, val py: Long
)
