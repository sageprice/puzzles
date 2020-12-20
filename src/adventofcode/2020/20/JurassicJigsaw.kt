package adventofcode.`2020`.`20`

import java.io.File

fun main() {
  val tiles: List<Tile> =
    File("src/adventofcode/2020/20/input.txt")
      .readText()
      .split("\n\n")
      .map { extractTile(it) }
  val tileMap = tiles.map { it.num to it }.toMap()

  // Part 1
  val adjacencies = getAdjacencyGraph(tiles)
  println("Part 1: " +
    adjacencies
      .filterValues { it.size == 2 }
      .keys
      .map { it.toLong() }
      .reduce { acc, v -> acc * v } )

  // Part 2
  val fullImage = constructImage(tileMap, adjacencies)
  var image = flattenImage(fullImage)
  val seaMonsterLines = listOf(
    "                  # ",
    "#    ##    ##    ###",
    " #  #  #  #  #  #   ")
  val seaMonster = seaMonsterLines.map { Regex(it.replace(" ", ".")) }
  image = fixOrientation(image, seaMonster)

  val smLength = seaMonsterLines.first().length
  val seaMonsters = findSeaMonsters(image, seaMonster, smLength)
  val seaMonsterPartCount = seaMonsterLines.sumBy { it.count { c -> c == '#' } }
  // Assuming sea monsters do not overlap. Which would be weird.
  val waterRoughness =
    image.map { it.count { c -> c == '#' } }.sum() - seaMonsterPartCount * seaMonsters.size
  println("Part 2: $waterRoughness")
}

private fun findSeaMonsters(
  image: List<String>,
  seaMonster: List<Regex>,
  smLength: Int
): List<Pair<Int, Int>> {
  val sms = mutableListOf<Pair<Int,Int>>()
  for (y in 0 until image.size - seaMonster.size) {
    for (x in 0 until image[0].length - smLength) {
      val matches = seaMonster.mapIndexed { i, r -> r.matchEntire(image[y + i].substring(x until x + smLength)) }
      if (matches.all { it != null }) {
        sms.add(Pair(y, x))
      }
    }
  }
  return sms
}

private fun fixOrientation(
  image: List<String>,
  seaMonster: List<Regex>
): List<String> {
  var fixedImage = image
  for (rotation in 1..4) {
    if (findSeaMonsters(fixedImage, seaMonster, seaMonster.first().pattern.length).isNotEmpty()) {
      return fixedImage
    }
    if (findSeaMonsters(fixedImage.reversed(), seaMonster, seaMonster.first().pattern.length).isNotEmpty()) {
      return fixedImage.reversed()
    }
    fixedImage = rotateSquareString(fixedImage)
  }
  error("Could not find valid rotation")
}

private fun constructImage(
  tileMap: Map<Int, Tile>,
  adjacencies: Map<Int, List<Int>>
): List<List<Tile>> {
  // Pick a start tile arbitrarily. We'll re-orient once this is done.
  var startCorner = tileMap[adjacencies.filterValues { it.size == 2 }.keys.first()] ?: error("Missing start tile")
  val startAdjacencies = adjacencies[startCorner.num]!!.map { tileMap[it]!! }
  while (!(startAdjacencies.any { startCorner.isLeftOf(it) != null }
        && startAdjacencies.any { startCorner.isAbove(it) != null })) {
    startCorner = startCorner.rotate()
  }
  val used = mutableSetOf(startCorner.num)
  val fullImage = mutableListOf(mutableListOf(startCorner))
  var isEndOfRow = false
  while (used.size != tileMap.size) {
    if (!isEndOfRow) {
      val previousTile = fullImage.last().last()
      val nextTile: Tile =
        adjacencies[previousTile.num]!!
          .mapNotNull { tileMap[it] }
          .filter { !used.contains(it.num) }
          .mapNotNull { previousTile.isLeftOf(it) }
          .first()
      fullImage.last().add(nextTile)
      used.add(nextTile.num)
      isEndOfRow = adjacencies[nextTile.num]!!.size < adjacencies[previousTile.num]!!.size
    } else {
      val previousTile = fullImage.last().first()
      val nextTile: Tile =
        adjacencies[previousTile.num]!!
          .mapNotNull { tileMap[it] }
          .filter { !used.contains(it.num) }
          .mapNotNull { previousTile.isAbove(it) }
          .first()
      fullImage.add(mutableListOf(nextTile))
      used.add(nextTile.num)
      isEndOfRow = false
    }
  }
  return fullImage
}

private fun flattenImage(tiles: List<List<Tile>>): List<String> {
  val lines = mutableListOf<String>()
  tiles.forEach { tileRow ->
    val tileDatas: List<List<String>> =
      tileRow
        .map {
          it
            .data
            .subList(1, it.data.size - 1)
            .map { row -> row.substring(1, row.length - 1) }
        }
    tileDatas[0].indices.forEach { i ->
      var s = ""
      for (tile in tileDatas) {
        s += tile[i]
      }
      lines.add(s)
    }
  }
  return lines
}

private fun Tile.isLeftOf(other: Tile): Tile? {
  var o = other
  repeat(4) {
    if (right == o.left) return o
    else if (right == o.left.reversed()) return o.flipVertical()
    o = o.rotate()
  }
  return null
}

private fun Tile.isAbove(other: Tile): Tile? {
  var o = other
  repeat(4) {
    if (bottom == o.top) return o
    else if(bottom == o.top.reversed()) return o.flipHorizontal()
    o = o.rotate()
  }
  return null
}

private fun getAdjacencyGraph(tiles: List<Tile>): Map<Int, List<Int>> {
  val adjacencies = mutableMapOf<Int, MutableList<Int>>()
  for (t in tiles) {
    adjacencies[t.num] = mutableListOf()
  }
  for (i in tiles.indices) {
    val t1 = tiles[i]
    val aSides: List<String> =
      listOf(t1.top, t1.bottom, t1.left, t1.right)
        .flatMap { listOf(it, it.reversed()) }
    for (j in i+1 until tiles.size) {
      val t2 = tiles[j]
      val bSides: List<String> = listOf(t2.top, t2.bottom, t2.left, t2.right)
      for (a in aSides) for (b in bSides) {
        if (a.zip(b).all { (x,y) -> x == y }) {
          adjacencies[t1.num]!!.add(t2.num)
          adjacencies[t2.num]!!.add(t1.num)
        }
      }
    }
  }
  return adjacencies
}

private fun extractTile(tile: String): Tile {
  val lines = tile.split("\n")
  val tileNum: Int =
    Regex("(\\d{4})")
      .find(lines[0])!!
      .groups[0]!!
      .value
      .toInt()
  val data = lines.subList(1, lines.size)
  val top = lines[1]
  val bottom = lines.last()
  val left = data.map { it.first() }.joinToString("")
  val right = data.map { it.last() }.joinToString("")
  return Tile(tileNum, data, top=top, right=right, bottom=bottom, left=left)
}

data class Tile(
  val num: Int,
  val data: List<String>,
  val top: String,
  val right: String,
  val bottom: String,
  val left: String
)

private fun Tile.flipHorizontal(): Tile {
  return Tile(
    num,
    data.map { it.reversed() },
    top = top.reversed(),
    left = right,
    right = left,
    bottom = bottom.reversed())
}

private fun Tile.flipVertical(): Tile {
  return Tile(
    num,
    data = data.reversed(),
    top = bottom,
    left = left.reversed(),
    right = right.reversed(),
    bottom = top)
}

private fun Tile.rotate(): Tile {

  return Tile(
    num,
    data = rotateSquareString(data),
    top = left.reversed(),
    left = bottom,
    bottom = right.reversed(),
    right = top
  )
}

private fun rotateSquareString(strs: List<String>): List<String> {
  val newData: MutableList<CharArray> = strs.map { it.toCharArray() }.toMutableList()
  for (i in 1 until newData.size) {
    for (j in 0 until i) {
      val t = strs[i][j]
      newData[i][j] = newData[j][i]
      newData[j][i] = t
    }
  }
  return newData.map { it.reversed().joinToString("") }
}