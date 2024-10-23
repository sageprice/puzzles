package adventofcode.`2016`.`02`

import java.io.File

private val KEYPAD = listOf(
  "123",
  "456",
  "789"
)

private val BIG_KEYPAD = listOf(
  "  1  ",
  " 234 ",
  "56789",
  " ABC ",
  "  D  "
)

fun main() {
  val input = File("src/adventofcode/2016/02/input.txt").readLines()
  var (r, c) = 1 to 1
  for (line in input) {
    val p = getNumber(line, r to c, KEYPAD)
    r = p.first
    c = p.second
    print(KEYPAD[r][c])
  }
  println()

  r = 2
  c = 0
  for (line in input) {
    val p = getNumber(line, r to c, BIG_KEYPAD)
    r = p.first
    c = p.second
    print(BIG_KEYPAD[r][c])
  }
  println()
}

private fun getNumber(dirs: String, rowAndColumn: Pair<Int, Int>, keypad: List<String>): Pair<Int, Int> {
  var (r, c) = rowAndColumn
  for (d in dirs) {
    when (d) {
      'U' -> if (r-1 in keypad.indices && keypad[r-1][c] != ' ') r -= 1
      'D' -> if (r+1 in keypad.indices && keypad[r+1][c] != ' ') r += 1
      'L' -> if (c-1 in keypad[r].indices && keypad[r][c-1] != ' ') c -= 1
      'R' -> if (c+1 in keypad[r].indices && keypad[r][c+1] != ' ') c += 1
    }
  }
  return r to c
}
