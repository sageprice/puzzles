package adventofcode.`2015`.`25`

import java.io.File

private const val INITIAL = 20151125L
private const val MULT = 252533L
private const val MOD = 33554393L

fun main() {
  val input = File("src/adventofcode/2015/25/input.txt").readText().split(" ")
  val targetRow = input[input.size - 3].substring(0, 4).toInt()
  val targetCol = input.last().substring(0, 4).toInt()

  var r = 1
  var c = 1
  var x = INITIAL
  while (r != targetRow || c != targetCol) {
    if (r == 1) {
      r += c
      c = 1
    } else {
      r -= 1
      c += 1
    }
    x = (x * MULT) % MOD
  }
  println(x)
}