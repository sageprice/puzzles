package adventofcode.`2021`.`18`

import java.io.File
import kotlin.math.ceil

fun main() {
  val snailfishNums = File("src/adventofcode/2021/18/input.txt")
    .readLines()
    .map { extractSnailfishNum(it).first }

  // Part 1
  println(snailfishNums.reduce { a, b -> a + b }.magnitude())

  // Part 2
  var best = -1L
  for (i in snailfishNums.indices) {
    for (j in i+1 until snailfishNums.size) {
      val a = (snailfishNums[i] + snailfishNums[j]).magnitude()
      val b = (snailfishNums[j] + snailfishNums[i]).magnitude()
      if (a > best) best = a
      if (b > best) best = b
    }
  }
  println(best)
}

private sealed class Node {
  fun magnitude(): Long {
    return when (this) {
      is Leaf -> data.toLong()
      is Internal -> 3 * left.magnitude() + 2 * right.magnitude()
    }
  }

  operator fun plus(other: Node): Node {
    var out: Node = Internal(this, other)
    while (true) { // Keep exploding and splitting until there is nothing left to do.
      val exploded = explode(out)
      out = if (exploded != null) {
        exploded.node
      } else {
        val splitUp = split(out) ?: return out
        splitUp
      }
    }
  }
}

private data class Internal(val left: Node, val right: Node): Node()

private data class Leaf(val data: Int): Node()

private fun extractSnailfishNum(str: String): Pair<Node, Int> {
  return if (str[0] == '[') {
    val (left, leftEndIndex) = extractSnailfishNum(str.substring(1)) // [
    val (right, endIndex) = extractSnailfishNum(str.substring(2 + leftEndIndex)) // [ + ,
    Pair(Internal(left, right), leftEndIndex + endIndex + 3) // [ + , + ]
  } else {
    Pair(Leaf(data = str[0].toString().toInt()), 1)
  }
}

/** Return type for {@code explode}. */
private data class Explosion(val leftAdd: Int?, val node: Node, val rightAdd: Int?)

/**
 * Explodes the first explode-able internal node in the given {@code node}.
 * Returns null when no node needs to explode.
 */
private fun explode(node: Node, depth: Int = 0): Explosion? {
  when (node) {
    is Leaf -> return null // no changes
    is Internal -> {
      if (depth >= 4) { // Perform an explosion
        if (node.left is Leaf && node.right is Leaf) {
          return Explosion(node.left.data, Leaf(0), node.right.data)
        } else {
          error("Cannot explode node: $node")
        }
      } else {
        val leftExplosion = explode(node.left, depth + 1)
        if (leftExplosion != null) {
          val (l, n, r) = leftExplosion
          return if (r != null) {
            val newRight = propagateRightRemains(node.right, r)
            Explosion(l, Internal(n, newRight), null)
          } else {
            Explosion(l, Internal(n, node.right), r)
          }
        } else { // we only check the right if left did not explode
          val rightExplosion = explode(node.right, depth + 1)
          if (rightExplosion != null) {
            val (l, n, r) = rightExplosion
            return if (l != null) {
              val newLeft = propagateLeftRemains(node.left, l)
              Explosion(null, Internal(newLeft, n), r)
            } else {
              Explosion(l, Internal(node.left, n), r)
            }
          }
        }
        return null // no changes
      }
    }
  }
}

/** Adds {@param x} to the furthest left {@code Leaf} in {@param node}. */
private fun propagateRightRemains(node: Node, x: Int): Node {
  return when (node) {
    is Leaf -> Leaf(node.data + x)
    is Internal -> {
      Internal(propagateRightRemains(node.left, x), node.right)
    }
  }
}

/** Adds {@param x} to the furthest right {@code Leaf} in {@param node}. */
private fun propagateLeftRemains(node: Node, x: Int): Node {
  return when (node) {
    is Leaf -> Leaf(node.data + x)
    is Internal -> {
      Internal(node.left, propagateLeftRemains(node.right, x))
    }
  }
}

/**
 * Splits the first splittable leaf node in the given {@param node}.
 * Returns null when no node needs to be split.
 */
private fun split(node: Node): Node? {
  return when (node) {
    is Leaf -> {
      if (node.data >= 10) Internal(Leaf(node.data / 2), Leaf(ceil(node.data / 2.0).toInt()))
      else null
    }
    is Internal -> {
      val leftSplit = split(node.left)
      if (leftSplit != null) {
        Internal(leftSplit, node.right)
      } else {
        val rightSplit = split(node.right)
        if (rightSplit != null) Internal(node.left, rightSplit)
        else null
      }
    }
  }
}