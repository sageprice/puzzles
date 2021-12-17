package adventofcode.`2021`.`17`

import java.io.File
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {
  val text = File("src/adventofcode/2021/17/input.txt").readText()

  val (xText, yText) = text.split(": ").last().split(", ")
  val xs = xText.substring(2).split("..").map { it.toInt() }
  val xRange = xs.first()..xs.last()
  val ys = yText.substring(2).split("..").map { it.toInt() }
  val yRange = ys.first()..ys.last()

  // Part 1
  val minXSpeed = sqrt(xs[0] * 2.0).roundToInt()
  val initialVs = mutableListOf<Pair<Int, Int>>()
  for (vy in ys.first()..500) for (vx in (minXSpeed-5)..xs.last()) {
    val landing = launchAtZone(xRange, yRange, Pair(vx, vy))
    if (landing != null) initialVs.add(Pair(vx, vy))
  }
  val vyMax = initialVs.maxOf { (_, vy) -> vy }
  println(vyMax * (vyMax+1) / 2)

  // Part 2
  println(initialVs.size)
}

private fun launchAtZone(xs: IntRange, ys: IntRange, velocity: Pair<Int, Int>): Pair<Int, Int>? {
  var (vx, vy) = velocity
  var x = 0
  var y = 0
  while (x < xs.last() && y > ys.first()) {
    x += vx
    if (vx > 0) --vx
    y += vy--
    if (x in xs && y in ys) return Pair(x, y)
  }
  return null
}