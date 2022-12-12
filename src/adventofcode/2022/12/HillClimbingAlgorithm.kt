package adventofcode.`2022`.`12`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val topoMap = File("src/adventofcode/2022/12/input.txt")
    .readLines()
    .map { it.toList() }

  val start = coordsAtElevation('S', topoMap).first()
  val end = coordsAtElevation('E', topoMap).first()

  println(hikeTrail(listOf(start), topoMap)[end])

  println(hikeTrail(coordsAtElevation('a', topoMap), topoMap)[end])
}

private fun coordsAtElevation(elevation: Char, topoMap: List<List<Char>>): List<Pair<Int, Int>> =
  topoMap.indices.flatMap { i ->
    topoMap[i].indices.map { j -> if (topoMap[i][j] == elevation) Pair(i, j) else null }
  }.filterNotNull()

private fun hikeTrail(
  start: List<Pair<Int, Int>>,
  topoMap: List<List<Char>>
): MutableMap<Pair<Int, Int>, Int> {
  val distance = mutableMapOf<Pair<Int, Int>, Int>()
  for (i in topoMap.indices) for (j in topoMap[i].indices) distance[Pair(i, j)] = Int.MAX_VALUE
  val next = PriorityQueue<Pair<Int, Int>>(Comparator.comparing { p -> distance[p]!! })
  for (p in start) {
    distance[p] = 0
    next.add(p)
  }
  while (next.isNotEmpty()) {
    val p = next.poll()
    nextPoints(p, topoMap).forEach { c ->
      val elevationChange =
        getElevationChange(c, topoMap) - getElevationChange(p, topoMap)
      if (elevationChange <= 1 && distance[c]!! > distance[p]!! + 1) {
        distance[c] = distance[p]!! + 1
        next.add(c)
      }
    }
  }
  return distance
}

private fun getElevationChange(p: Pair<Int, Int>, elevation: List<List<Char>>): Char =
  when (elevation[p.first][p.second]) {
    'S' -> 'a' - 1
    'E' -> 'z' + 1
    else -> elevation[p.first][p.second]
  }

private fun nextPoints(coord: Pair<Int, Int>, topoMap: List<List<Char>>): List<Pair<Int, Int>> {
  val next = mutableListOf<Pair<Int, Int>>()
  val (r, c) = coord
  if (r > 0) next.add(Pair(r-1, c))
  if (r < topoMap.size - 1) next.add(Pair(r+1, c))
  if (c > 0) next.add(Pair(r, c-1))
  if (c < topoMap[r].size - 1) next.add(Pair(r, c+1))
  return next
}
