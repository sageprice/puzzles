package adventofcode.`2023`.`22`

import java.io.File

fun main() {
  val slabs =
      File("src/adventofcode/2023/22/input.txt")
          .readLines()
          .map { parseSlab(it) }
          .sortedBy { s -> s.blocks.minOf { it.z } }
  // Part 1
  val (lowered, supports) = lowerSlabs(slabs)
  println(countSafeToDisintegrate(lowered.size, supports))

  // Part 2
  println(sumChainReactions(supports))
}

private fun sumChainReactions(supports: List<Pair<String, String>>): Int {
  val namedSlabs = supports.map { listOf(it.first, it.second) }.flatten().distinct()
  return namedSlabs.sumOf { slab ->
    countDisintegrations(
        slab,
        supports.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } },
        supports.groupBy { it.second }.mapValues { (_, v) -> v.map { it.first } }) }
}

private fun countDisintegrations(
    slab: String, supportedBy: Map<String, List<String>>, supporting: Map<String, List<String>>): Int {
  val crumblingSupportedBy = supportedBy.mapValues { (_, v) -> v.toMutableList() }.toMutableMap()
  val crumblingSupporting = supporting.mapValues { (_, v) -> v.toMutableList() }.toMutableMap()
  val toRemove = mutableListOf(slab)
  var removedCount = -1
  while (toRemove.isNotEmpty()) {
    removedCount++
    val goner = toRemove.removeFirst()
    val toUpdate = crumblingSupporting.remove(goner)
    toUpdate?.forEach { next ->
      crumblingSupportedBy[next]?.remove(goner)
      if (crumblingSupportedBy[next]?.isEmpty() == true) {
        crumblingSupportedBy.remove(next)
        toRemove.add(next)
      }
    }
  }
  return removedCount
}

private fun countSafeToDisintegrate(slabCount: Int, supports: List<Pair<String, String>>): Int {
  val namedBricks = supports.map { listOf(it.first, it.second) }.flatten().distinct()
  val supportedBy = supports.groupBy { it.first }.mapValues { (_, v) -> v.map { it.second } }
  val supporting = supports.groupBy { it.second }.mapValues { (_, v) -> v.map { it.first } }
  var safeToDisintegrateCount = 0
  for (brick in namedBricks) {
    if (brick !in supporting) safeToDisintegrateCount++ else {
      val supportedBricks = supporting[brick]
      if (supportedBricks != null && supportedBricks.all { above -> (supportedBy[above]?.size ?: 0) > 1 }) {
        safeToDisintegrateCount++
      }
    }
  }
  return safeToDisintegrateCount + slabCount - namedBricks.size
}

private fun lowerSlabs(slabs: List<Slab>): Pair<List<Pair<String, Slab>>, List<Pair<String, String>>> {
  val settledBlocks = mutableMapOf<Block, String>()
  val restingSlabs = mutableListOf<Pair<String, Slab>>()
  val supports = mutableListOf<Pair<String, String>>()
  // Name is for debugging here, but more in use for graph-traversal in part 2.
  // Format doesn't really matter, so auto-increment from a value to not collide
  // with the actual indices where the blocks fall is good enough.
  var name = 100
  for (slab in slabs) {
    name++
    var bricks = slab.blocks
    var lowered = bricks.map { Block(it.x, it.y, it.z-1) }
    while (lowered.all { it.z > 0 } && lowered.none { it in settledBlocks }) {
      bricks = lowered
      lowered = bricks.map { Block(it.x, it.y, it.z-1) }
    }
    lowered.filter { it in settledBlocks }.forEach { l -> supports.add(Pair(name.toString(), settledBlocks[l]!!)) }
    bricks.forEach { settledBlocks[it] = name.toString() }
    restingSlabs.add(Pair(name.toString(), Slab(bricks)))
  }
  return Pair(restingSlabs, supports.distinct())
}

private fun parseSlab(str: String): Slab {
  val (b1, b2) = str.split("~")
  val (x1, y1, z1) = b1.split(",")
  val (x2, y2, z2) = b2.split(",")
  val blocks = mutableListOf<Block>()
  (x1.toInt()..x2.toInt()).forEach { x ->
    (y1.toInt()..y2.toInt()).forEach { y ->
      (z1.toInt()..z2.toInt()).forEach { z -> blocks.add(Block(x, y, z)) }
    }
  }
  return Slab(blocks)
}

private data class Slab(val blocks: List<Block>)

private data class Block(val x: Int, val y: Int, val z: Int)
