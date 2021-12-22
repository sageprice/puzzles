package adventofcode.`2021`.`22`

import java.io.File
import java.math.BigInteger
import kotlin.math.max
import kotlin.math.min

fun main() {
  val lines = File("src/adventofcode/2021/22/input.txt").readLines()
  val boxes = lines.map { line ->
    val (isOn, box) = line.split(" ")
    val (xs, ys, zs) = box.split(",").map { it.substring(2) }
    val (xLow, xHigh) = xs.split("..").map { it.toInt() }
    val (yLow, yHigh) = ys.split("..").map { it.toInt() }
    val (zLow, zHigh) = zs.split("..").map { it.toInt() }
    Box(isOn == "on", xLow..xHigh, yLow..yHigh, zLow..zHigh)
  }

  // Part 1
  val rangeLimit = -50..50
  val limitedBoxes = boxes.filter {
    rangeLimit.containsAll(it.xs) && rangeLimit.containsAll(it.ys) && rangeLimit.containsAll(it.zs)
  }
  println(getProperBoxes(limitedBoxes).entries.sumOf { (t, count) ->
    val (xs, ys, zs) = t
    BigInteger.valueOf(count * xs.length() * ys.length() * zs.length())
  })

  // part 2
  println(getProperBoxes(boxes).entries.sumOf { (t, count) ->
    val (xs, ys, zs) = t
    count * xs.length() * ys.length() * zs.length()
  })
}

// Using inclusion-exclusion principle
private fun getProperBoxes(boxes: List<Box>): Map<Triple<IntRange, IntRange, IntRange>, Long> {
  var processedBoxes = mutableMapOf<Triple<IntRange, IntRange, IntRange>, Long>()
  for (box in boxes) {
    val entries = processedBoxes.entries.toList()
    val intersections = mutableMapOf<Triple<IntRange, IntRange, IntRange>, Long>()
    for ((t, v) in entries) {
      val overlap = box.getOverlap(t)
      if (overlap != null) intersections[overlap] = -v +
        intersections.computeIfAbsent(overlap) { 0L }
    }
    if (box.isOn) {
      intersections[Triple(box.xs, box.ys, box.zs)] = 1 +
        intersections.computeIfAbsent(Triple(box.xs, box.ys, box.zs)) { 0 }
    }
    for ((k, v) in intersections) processedBoxes[k] = v + processedBoxes.computeIfAbsent(k) { 0L }
    processedBoxes = processedBoxes.filter { it.value != 0L }.toMutableMap()
  }
  return processedBoxes
}

private fun Box.getOverlap(
  t: Triple<IntRange, IntRange, IntRange>
): Triple<IntRange, IntRange, IntRange>? {
  val (bx, by, bz) = t
  return if (xs.containsAny(bx) && ys.containsAny(by) && zs.containsAny(bz)) {
    Triple(xs.getOverlap(bx), ys.getOverlap(by), zs.getOverlap(bz))
  } else null
}

private fun IntRange.getOverlap(o:IntRange): IntRange {
  assert(this.containsAny(o))
  return max(this.first, o.first)..min(this.last, o.last)
}

private fun IntRange.containsAny(o: IntRange): Boolean =
  max(this.first, o.first) <= min(this.last, o.last)

private fun IntRange.containsAll(o: IntRange): Boolean = o.first >= first && o.last <= last

private fun IntRange.length(): Long = last - first + 1L

private data class Box(
  val isOn: Boolean,
  val xs: IntRange,
  val ys: IntRange,
  val zs: IntRange
)