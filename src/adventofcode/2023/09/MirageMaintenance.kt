package adventofcode.`2023`.`09`

import java.io.File

fun main() {
  val readings =
    File("src/adventofcode/2023/09/input.txt")
      .readLines()
      .map { it.split(" ").map { n -> n.toLong() } }

  println(readings.sumOf { getNextValue(it) })
  println(readings.sumOf { getPreviousValue(it) })
}

private fun getNextValue(xs: List<Long>): Long {
  val gaps = mutableListOf(xs)
  while (gaps.last().any { it != 0L }) {
    val next = (0..gaps.last().size - 2).map {
      gaps.last()[it+1] - gaps.last()[it]
    }
    gaps.add(next)
  }
  val tails = mutableListOf<Long>(0)
  (gaps.size - 2 downTo  0).forEach { i ->
    tails.add(gaps[i].last() + tails.last())
  }
  return tails.last()
}

private fun getPreviousValue(xs: List<Long>): Long {
  val gaps = mutableListOf(xs)
  while (gaps.last().any { it != 0L }) {
    val next = (0..gaps.last().size - 2).map {
      gaps.last()[it+1] - gaps.last()[it]
    }
    gaps.add(next)
  }
  val heads = mutableListOf<Long>(0)
  (gaps.size - 2 downTo  0).forEach { i ->
    heads.add(gaps[i].first() - heads.last())
  }
  return heads.last()
}