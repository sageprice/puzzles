package adventofcode.`2016`.`06`

import java.io.File

fun main() {
  val lines = File("src/adventofcode/2016/06/input.txt").readLines()
  println(lines.first().indices.map { getMostCommonLetter(lines, it) }.joinToString(""))
  println(lines.first().indices.map { getLeastCommonLetter(lines, it) }.joinToString(""))
}

private fun getMostCommonLetter(ls: List<String>, index: Int): Char =
  ls.map { it[index] }.groupBy { it }.maxBy { it.value.size }.key

private fun getLeastCommonLetter(ls: List<String>, index: Int): Char =
  ls.map { it[index] }.groupBy { it }.minBy { it.value.size }.key