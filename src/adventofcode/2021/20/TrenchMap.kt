package adventofcode.`2021`.`20`

import java.io.File

fun main() {
  val (algorithm, image) = File("src/adventofcode/2021/20/input.txt")
    .readText()
    .split("\n\n")

  val map = mutableMapOf<Pair<Int, Int>, Char>()
  val lines = image.split("\n")
  for (y in lines.indices) {
    for (x in lines[y].indices) {
      map[Pair(x, y)] = lines[y][x]
    }
  }
  var imageMap = map.toMap()

  // Part 1
  repeat(2) { imageMap = step(imageMap, algorithm, it + 1) }
  println(imageMap.values.count { it == '#' })

  // Part 2
  repeat(48) { imageMap = step(imageMap, algorithm, it + 1) }
  println(imageMap.values.count { it == '#' })
}

fun step(map: Map<Pair<Int, Int>, Char>, algorithm: String, iteration: Int): Map<Pair<Int, Int>, Char> {
  val output = mutableMapOf<Pair<Int, Int>, Char>()
  val xMin: Int = map.keys.minOf { it.first }
  val xMax = map.keys.maxOf { it.first }
  val yMin = map.keys.minOf { it.second }
  val yMax = map.keys.maxOf { it.second }

  for (x in xMin-1..xMax+1) {
    for (y in yMin-1..yMax+1) {
      val neighbors = IntArray(9)
      for (yAdj in -1..1) for (xAdj in -1..1) {
        val k = Pair(x + xAdj, y + yAdj)
        if (!map.containsKey(k)) {
          if (algorithm[0] == '#') {
            // Infinite grid is flipping back and forth due to 0 -> #, hack to use iteration
            // count as a way of deciding what infinite grid state is.
            neighbors[3 * (yAdj + 1) + xAdj + 1] = if (iteration % 2 == 0) 1 else 0
          }
        } else if (map[k]!! == '#') neighbors[3*(yAdj + 1) + xAdj+1] = 1
      }
      output[Pair(x, y)] = algorithm[binaryValue(neighbors)]
    }
  }
  return output
}

private fun binaryValue(bin: IntArray): Int {
  var x = 0
  for (b in bin) {
    x *= 2
    if (b == 1) x++
  }
  return x
}

private fun printImage(map: Map<Pair<Int, Int>, Char>) {
  val xMin: Int = map.keys.minOf { it.first }
  val xMax = map.keys.maxOf { it.first }
  val yMin = map.keys.minOf { it.second }
  val yMax = map.keys.maxOf { it.second }
  for (y in yMin..yMax) {
    for (x in xMin..xMax) {
      print(map[Pair(x, y)])
    }
    println()
  }
}