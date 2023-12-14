package adventofcode.`2023`.`14`

import java.io.File

fun main() {
  val rocks = File("src/adventofcode/2023/14/input.txt").readLines().map { it.toList() }
  val rowCount = rocks.size
  println(getLoad(rollRocksNorth(rocks), rowCount))
  println(getLoad(rollTimes(rocks, 1_000_000_000), rowCount))
}

private fun rollTimes(rocks: List<List<Char>>, k: Int): List<List<Char>> {
  val seen = mutableMapOf<List<List<Char>>, Int>()
  var rolled = rocks
  var i = 0
  while (rolled !in seen) {
    seen[rolled] = i
    rolled = rollRocksNorth(rolled)
    rolled = rollRocksWest(rolled)
    rolled = rollRocksSouth(rolled)
    rolled = rollRocksEast(rolled)
    ++i
  }
  val iterationLoop = i - (seen[rolled] ?: error("Can't find key despite exiting loop"))
  i += iterationLoop * ((k - i) / iterationLoop)
  while (i < k) {
    ++i
    rolled = rollRocksNorth(rolled)
    rolled = rollRocksWest(rolled)
    rolled = rollRocksSouth(rolled)
    rolled = rollRocksEast(rolled)
  }
  return rolled
}

private fun rollRocksNorth(start: List<List<Char>>): List<List<Char>> {
  val rolled = MutableList(start.size) { mutableListOf<Char>() }
  for (c in start.first().indices) {
    var i = 0
    var rockCount = 0
    var spaceCount = 0
    for (r in 0..start.size) {
      if (r == start.size || start[r][c] == '#') {
        repeat(rockCount) {
          rolled[i++].add('O')
        }
        rockCount = 0
        repeat(spaceCount) {
          rolled[i++].add('.')
        }
        spaceCount = 0
        if (r < start.size) rolled[i++].add('#')
      } else {
        if (start[r][c] == 'O') rockCount++
        if (start[r][c] == '.') spaceCount++
      }
    }
  }
  return rolled
}

private fun rollRocksSouth(start: List<List<Char>>): List<List<Char>> {
  return rollRocksNorth(start.reversed()).reversed()
}

private fun rollRocksEast(start: List<List<Char>>): List<List<Char>> {
  val output = mutableListOf<List<Char>>()
  for (row in start) {
    val newRow = mutableListOf<Char>()
    var rockCount = 0
    var spaceCount = 0
    for (i in 0..row.size) {
      if (i == row.size || row[i] == '#') {
        repeat(spaceCount) { newRow.add('.') }
        spaceCount = 0
        repeat(rockCount) { newRow.add('O') }
        rockCount = 0
        if (i < row.size) newRow.add('#')
      } else {
        if (row[i] == 'O') rockCount++
        if (row[i] == '.') spaceCount++
      }
    }
    output.add(newRow)
  }
  return output
}

private fun rollRocksWest(start: List<List<Char>>): List<List<Char>> {
  return rollRocksEast(
    start.map { it.reversed() }
  ).map { it.reversed() }
}

private fun getLoad(rockMap: List<List<Char>>, len: Int): Long {
  var s = 0L
  for (r in rockMap.indices) for (rock in rockMap[r]) {
    if (rock == 'O') s += len - r
  }
  return s
}
