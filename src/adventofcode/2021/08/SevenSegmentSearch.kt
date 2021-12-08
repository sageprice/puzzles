package adventofcode.`2021`.`08`

import java.io.File

fun main() {
  val entries: List<Entry> =
    File("src/adventofcode/2021/08/input.txt")
      .readLines()
      .map { it.split(" | ") }
      .map { Entry(it.first().split(" "), it.last().split(" ")) }

  // Part 1
  println(
    entries.map { it.output.count { e ->
      e.length == 2 || e.length == 3 || e.length == 4 || e.length == 7
    } }.sum())

  // Part 2
  println(entries.sumBy { e ->
    e.output.map { o ->
      solve(e).entries
        .first { (_, v) -> v.containsAll(o.toSet()) && o.toSet().containsAll(v) }
        .key
    }.reduce { a, x -> 10*a + x }
  })
}

private enum class Segment {
  TOP,
  UP_LEFT,
  UP_RIGHT,
  CENTER,
  DOWN_LEFT,
  DOWN_RIGHT,
  BOTTOM;
}
private data class Entry(val input: List<String>, val output: List<String>)

private fun solve(entry: Entry): Map<Int, Set<Char>> {
  val encodings = mutableMapOf<Int, Set<Char>>()
  val patterns = (entry.input + entry.output).map { it.toSet() }
  val segments = mutableMapOf<Segment, Char>()
  val one = patterns.first { it.size == 2 }.toSet()
  encodings[1] = one
  val seven = patterns.first { it.size == 3 }.toSet()
  encodings[7] = seven
  val four = patterns.first { it.size == 4 }.toSet()
  encodings[4] = four
  val eight = patterns.first { it.size == 7 }.toSet()
  encodings[8] = eight
  // SEVEN contains ONE, plus top => we can get top bar directly.
  segments[Segment.TOP] = seven.minus(one).first()
  // NINE contains all of FOUR, plus top and bottom => we can get bottom bar directly.
  segments[Segment.BOTTOM] = patterns
    .filter { it.size == 6 }
    .map { it.minus(segments[Segment.TOP]).minus(four) }
    .first { it.size == 1 }.first()!!.toChar()
  // SIX is the only 6-segment number without up-right, which is in ONE.
  val six = patterns.filter { it.size == 6 }.first { !it.containsAll(one) }
  encodings[6] = six
  // EIGHT is SIX, plus up-right
  segments[Segment.UP_RIGHT] = eight.minus(six).first()
  // We know up-right, and we have ONE, so we know bottom-right.
  segments[Segment.DOWN_RIGHT] = one.minus(segments[Segment.UP_RIGHT]).first()!!
  // At this point we know all segments of THREE except the center.
  val three = patterns.first { it.minus(segments.values).size == 1 }
  encodings[3] = three
  segments[Segment.CENTER] = three.minus(segments.values).first()
  // Once we know all of THREE, we know all of FOUR except the up-left.
  segments[Segment.UP_LEFT] = four.minus(three).first()
  // Oh hey we've got everything but down-left now. And eight has everything...
  segments[Segment.DOWN_LEFT] = eight.minus(segments.values).first()
  encodings[9] = encodings[8]!!.minus(segments[Segment.DOWN_LEFT]!!)
  encodings[0] = encodings[8]!!.minus(segments[Segment.CENTER]!!)
  encodings[5] = encodings[8]!!.minus(segments[Segment.UP_RIGHT]!!).minus(segments[Segment.DOWN_LEFT]!!)
  encodings[2] = encodings[8]!!.minus(segments[Segment.UP_LEFT]!!).minus(segments[Segment.DOWN_RIGHT]!!)
  assert(encodings.size == 10)
  return encodings
}