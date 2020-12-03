package adventofcode.`2020`.`03`

import java.io.File

fun main() {
  val map: List<CharArray> =
    File("src/adventofcode/2020/03/input.txt")
      .readLines()
      .map { it.toCharArray() }

  // Part 1
  println("Part 1: ${countTrees(map, dx=3)}")
  // Part 2
  println("Part 2: ${countTrees(map, dx=1) *
        countTrees(map, dx=3) *
        countTrees(map, dx=5) *
        countTrees(map, dx=7) *
        countTrees(map, dx=1, dy=2)}"
  )
}

fun countTrees(map: List<CharArray>, dx: Int, dy: Int = 1): Long {
  var x = 0
  var y = 0
  var treesHit = 0L
  while (y < map.size) {
    if (map[y][x] == '#') {
      treesHit++
    }
    x += dx
    y += dy
    if (x >= map[0].size) {
      x -= map[0].size
    }
  }
  return treesHit
}