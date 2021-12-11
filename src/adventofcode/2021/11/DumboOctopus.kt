package adventofcode.`2021`.`11`

import java.io.File
import java.lang.Integer.max
import kotlin.math.min

fun main() {
  var octopi: List<List<Octopus>> = readInput()

  // Part 1
  var totalFlashes = 0L
  repeat(100) {
    totalFlashes += step(octopi)
  }
  println(totalFlashes)

  // Part 2
  octopi = readInput() // reset
  var steps = 0
  while (octopi.flatten().any { it.energy != 0 } ) {
    steps++
    step(octopi)
  }
  println(steps)
}

private fun readInput(): List<List<Octopus>> {
  return File("src/adventofcode/2021/11/input.txt")
    .readLines()
    .map {
      it.map { d ->
        Octopus(d.toString().toInt(), false)
      }.toList()
    }
}

private fun step(octopi: List<List<Octopus>>): Long {
  var flashes = 0L
  val toFlash = mutableListOf<Pair<Int, Int>>()
  for (x in octopi.indices) {
    for (y in octopi[x].indices) {
      if (++octopi[x][y].energy > 9) toFlash.add(Pair(x, y))
    }
  }
  while (toFlash.isNotEmpty()) {
    val (x, y) = toFlash.removeAt(0)
    if (octopi[x][y].hasFlashed) continue
    octopi[x][y].hasFlashed = true
    flashes++
    for (i in max(0,x-1)..min(octopi.size-1, x+1)) {
      for (j in max(0,y-1)..min(octopi[x].size-1, y+1)) {
        if (++octopi[i][j].energy > 9 && !octopi[i][j].hasFlashed) toFlash.add(Pair(i, j))
      }
    }
  }
  for (x in octopi.indices) {
    for (y in octopi[x].indices) {
      if (octopi[x][y].energy > 9) {
        octopi[x][y].energy = 0
        octopi[x][y].hasFlashed = false
      }
    }
  }
  return flashes
}

private data class Octopus(var energy: Int, var hasFlashed: Boolean)