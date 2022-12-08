package adventofcode.`2022`.`08`

import java.io.File

fun main() {
  val treeHeights = File("src/adventofcode/2022/08/input.txt")
    .readLines()
    .map { it.toCharArray().map { d -> d.toString().toInt() } }

  val visible = mutableSetOf<Pair<Int, Int>>()
  for (i in treeHeights.indices) {
    var highest = -1
    // Visible from the left
    for (j in treeHeights[i].indices) {
      if (treeHeights[i][j] > highest) {
        visible.add(Pair(i, j))
        highest = treeHeights[i][j]
      }
    }
    highest = -1
    // Visible from the right
    for (j in treeHeights[i].indices.reversed()) {
      if (treeHeights[i][j] > highest) {
        visible.add(Pair(i, j))
        highest = treeHeights[i][j]
      }
    }
  }
  for (i in treeHeights[0].indices) {
    var highest = -1
    // Visible from above
    for (j in treeHeights.indices) {
      if (treeHeights[j][i] > highest) {
        visible.add(Pair(j, i))
        highest = treeHeights[j][i]
      }
    }
    highest = -1
    // Visible from below
    for (j in treeHeights.indices.reversed()) {
      if (treeHeights[j][i] > highest) {
        visible.add(Pair(j, i))
        highest = treeHeights[j][i]
      }
    }
  }
  println(visible.size)

  println(visible.maxOfOrNull { getScenicScore(it, treeHeights) })
}

private fun getScenicScore(tree: Pair<Int, Int>, heights: List<List<Int>>): Long {
  val h = heights[tree.first][tree.second]
  var left = 0L
  for (i in tree.second - 1 downTo 0) {
    left++
    if (heights[tree.first][i] >= h) break
  }
  var right = 0
  for (i in tree.second + 1 until heights[0].size) {
    right++
    if (heights[tree.first][i] >= h) break
  }
  var up = 0
  for (i in tree.first - 1 downTo 0) {
    up++
    if (heights[i][tree.second] >= h) break
  }
  var down = 0
  for (i in tree.first + 1 until heights.size) {
    down++
    if (heights[i][tree.second] >= h) break
  }
  return left*right*up*down
}
