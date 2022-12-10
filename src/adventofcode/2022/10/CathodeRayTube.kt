package adventofcode.`2022`.`10`

import java.io.File
import kotlin.math.abs

fun main() {
  val input = File("src/adventofcode/2022/10/input.txt")
    .readLines()

  var x = 1
  var cycle = 0
  var dx = 0
  var totalSignalStrength = 0
  var display = ""
  for (line in input) {
    cycle++
    display += if (abs(x - ((cycle - 1) % 40)) <= 1) "█" else " "
    if (cycle > 0 && (cycle % 40) == 0) display += "\r\n"
    if (cycle % 40 == 20) totalSignalStrength += cycle * x
    if (line.startsWith("addx")) {
      val (_, amt) = line.split(" ")
      dx = amt.toInt()
      cycle++
      display += if (abs(x - ((cycle-1) % 40)) <= 1) "█" else " "
      if (cycle > 0 && (cycle % 40) == 0) display += "\r\n"
      if (cycle % 40 == 20) totalSignalStrength += cycle * x
    }
    x += dx
    dx = 0
  }
  println(totalSignalStrength)
  println(display)
}