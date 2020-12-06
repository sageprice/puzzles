package adventofcode.`2020`.`06`

import java.io.File

fun main() {
  val lines: List<String> =
    File("src/adventofcode/2020/06/input.txt")
      .readLines()

  val allLetters = "abcdefghijklmnopqrstuvwxyz"

  var answers = mutableSetOf<Char>()
  var sharedAnswers = allLetters.toSet()
  val answerGroups = mutableListOf<Set<Char>>()
  val sharedAnswerGroups = mutableListOf<Set<Char>>()
  for (i in lines.indices) {
    if (lines[i].isBlank()) {
      answerGroups.add(answers)
      sharedAnswerGroups.add(sharedAnswers)
      answers = mutableSetOf()
      sharedAnswers = allLetters.toSet()
    } else {
      sharedAnswers = sharedAnswers.intersect(lines[i].toSet())
      lines[i].toCharArray().filter { it != '\n' }.forEach { answers.add(it) }
    }
  }

  println("Part 1: ${answerGroups.map { it.size }.sum()}")
  println("Part 2: ${sharedAnswerGroups.map { it.size }.sum()}")
}