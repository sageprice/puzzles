package adventofcode.`2024`.`10`

import java.io.File

fun main() {
  val topo = File("src/adventofcode/2024/10/input.txt").readLines().map { l ->
    l.toCharArray().map { x -> x.toString().toInt() }
  }
  val heights = getHeights(topo)
  // Note: both scores and ratings are calculated backwards (based on ending). Doesn't matter in practice.
  val routesFrom = getScores(topo)
  println(heights[9]?.sumOf { (r, c) -> routesFrom[r][c].size })
  val ratings = getRating(topo)
  println(heights[9]?.sumOf { (r, c) -> ratings[r][c] })
}

private fun getHeights(topo: List<List<Int>>): Map<Int, List<Pair<Int, Int>>> {
  val starts = mutableListOf<Pair<Int, Pair<Int, Int>>>()
  for (r in topo.indices) for (c in topo[r].indices) {
    starts.add(topo[r][c] to (r to c))
  }
  return starts.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } }
}

private fun getScores(topo: List<List<Int>>): Array<Array<MutableSet<Pair<Int, Int>>>> {
  val rMax = topo.size
  val cMax = topo.first().size
  val heights = getHeights(topo)
  val routesThrough = Array(topo.size) { Array(topo.first().size) { mutableSetOf<Pair<Int, Int>>() } }
  heights[0]?.forEach { (r, c) -> routesThrough[r][c].add(r to c) }
  for (i in 0..9) {
    heights[i]?.forEach { (r, c) ->
      getAdjacentPoints(r, c, rMax, cMax).forEach { (r1, c1) ->
        if (topo[r1][c1] == topo[r][c] + 1) routesThrough[r1][c1] += routesThrough[r][c]
      }
    }
  }
  return routesThrough
}

private fun getRating(topo: List<List<Int>>): Array<Array<Int>> {
  val rMax = topo.size
  val cMax = topo.first().size
  val heights = getHeights(topo)
  val routesThrough = Array(topo.size) { Array(topo.first().size) { 0 } }
  heights[0]?.forEach { (r, c) -> routesThrough[r][c] = 1 }
  for (i in 0..9) {
    heights[i]?.forEach { (r, c) ->
      getAdjacentPoints(r, c, rMax, cMax).forEach { (r1, c1) ->
        if (topo[r1][c1] == topo[r][c] + 1) routesThrough[r1][c1] += routesThrough[r][c]
      }
    }
  }
  return routesThrough
}

private fun getAdjacentPoints(r: Int, c: Int, rMax: Int, cMax: Int): List<Pair<Int, Int>> {
  return listOf(r-1 to c, r+1 to c, r to c+1, r to c-1).filter { (r, c) ->
    r in 0 until rMax && c in 0 until cMax
  }
}
