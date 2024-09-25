package adventofcode.`2015`.`18`

import java.io.File

fun main() {
  val lights =
    File("src/adventofcode/2015/18/input.txt")
      .readLines()
      .map { it.toList().map { c -> c == '#' } }
  var next = lights
  repeat(100) {
    next = getNextLights(next)
  }
  println(next.sumOf { row -> row.count { it } })

  next = lights
  repeat(100) {
    next = getNextLights(next, true)
  }
  println(next.sumOf { row -> row.count { it } })
}

private fun getNextLights(lights: List<List<Boolean>>, isPart2: Boolean = false): List<List<Boolean>> {
  val next = MutableList(lights.size) { MutableList(lights.first().size) { false } }
  for (r in next.indices) for (c in next[r].indices) {
    val adjacentLit = getAdjacentCount(lights, r, c, isPart2)
    if (adjacentLit == 3) next[r][c] = true
    if (adjacentLit == 2 && lights[r][c]) next[r][c] = true
    if (isPart2) {
      when {
        r == 0 && c == 0 -> next[r][c] = true
        r == 0 && c == lights.first().size - 1 -> next[r][c] = true
        r == lights.size - 1 && c == 0 -> next[r][c] = true
        r == lights.size - 1 && c == lights.first().size - 1 -> next[r][c] = true
      }
    }
  }
  return next
}

private fun getAdjacentCount(lights: List<List<Boolean>>, r: Int, c: Int, isPart2: Boolean): Int {
  var count = 0
  for (i in r-1..r+1) for (j in c-1..c+1) {
    if (i == r && j == c) continue
    if (i !in lights.indices || j !in lights.first().indices) continue
    if (isPart2) {
      when {
        i == 0 && j == 0 -> count++
        i == 0 && j == lights.first().size - 1 -> count++
        i == lights.size - 1 && j == 0 -> count++
        i == lights.size - 1 && j == lights.first().size - 1 -> count++
        else -> if (lights[i][j]) count++
      }
    } else {
      if (lights[i][j]) count++
    }
  }
  return count
}

// Debugging function
private fun printLights(lights: List<List<Boolean>>) {
  for (row in lights) {
    for (light in row) {
      print(if (light) '#' else '.')
    }
    println()
  }
}
