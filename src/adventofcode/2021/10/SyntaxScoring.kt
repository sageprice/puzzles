package adventofcode.`2021`.`10`

import java.io.File

fun main() {
  val lines: List<String> =
    File("src/adventofcode/2021/10/input.txt")
      .readLines()

  // Part 1
  var score = 0
  for (line in lines) {
    score += when (getSyntaxError(line)) {
      ')' -> 3
      ']' -> 57
      '}' -> 1197
      '>' -> 25137
      else -> continue
    }
  }
  println(score)

  // Part 2
  val scores = lines.filter { getSyntaxError(it) == null }
    .map { score(getCompletion(it)) }
    .sorted()
  println(scores[scores.size/2])
}

private fun getSyntaxError(chars: String): Char? {
  val processed = mutableListOf<Char>()
  for (c in chars) {
    when (c) {
      '(' -> processed.add(c)
      '[' -> processed.add(c)
      '{' -> processed.add(c)
      '<' -> processed.add(c)
      ')' -> if (processed.removeLast() != '(') return c
      ']' -> if (processed.removeLast() != '[') return c
      '}' -> if (processed.removeLast() != '{') return c
      '>' -> if (processed.removeLast() != '<') return c
      else -> error("Invalid char: $c")
    }
  }
  return null
}

private fun getCompletion(chars: String): List<Char> {
  val processed = mutableListOf<Char>()
  for (c in chars) {
    when (c) {
      '(' -> processed.add(c)
      '[' -> processed.add(c)
      '{' -> processed.add(c)
      '<' -> processed.add(c)
      // The strings are already vetted, so we can assume a match.
      else -> processed.removeLast()
    }
  }
  return processed.map { c ->
    when (c) {
      '(' -> ')'
      '[' -> ']'
      '{' -> '}'
      '<' -> '>'
      else -> error("Invalid char: $c")
    }
  }.reversed()
}

private fun score(chars: List<Char>): Long {
  var s = 0L
  for (c in chars) {
    s *= 5
    s += when(c) {
      ')' -> 1
      ']' -> 2
      '}' -> 3
      '>' -> 4
      else -> error("Invalid char: $c")
    }
  }
  return s
}