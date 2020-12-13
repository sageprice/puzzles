package adventofcode.`2020`.`13`

import java.io.File
import java.math.BigInteger

fun main() {
  val input: List<String> =
    File("src/adventofcode/2020/13/input.txt")
      .readLines()
  val arrivalTime = input[0].toInt()
  val ids = input[1].split(",").filter { it != "x" }.map { it.toInt() }

  // Part 1
  val answer = ids.map { Pair(it, (it * (1 + arrivalTime / it) - arrivalTime)) }.minBy { it.second }!!
  println("Part 1: " + answer.first * answer.second)

  // Part 2
  val busses: List<Pair<BigInteger, BigInteger>> =
    input[1]
      .split(",")
      .mapIndexed { i, v -> Pair(v, i) }
      .filter { it.first != "x" }
      .map { Pair(it.first.toBigInteger(), it.first.toBigInteger() - it.second.toBigInteger())}
  println("Part 2: " + crt(busses))
}

/**
 * Chinese Remainder Theorem, where input pairs (a,b) represent equations x = b mod a.
 * Implementation is based on: https://brilliant.org/wiki/chinese-remainder-theorem/
 */
private fun crt(relations: List<Pair<BigInteger, BigInteger>>): BigInteger {
  val sortedRelations = relations.sortedByDescending { it.first }
  var (a, b) = sortedRelations[0]
  for (i in 1 until sortedRelations.size) {
    val (m, n) = sortedRelations[i]
    val a1 = a % m
    var b1 = b % m
    b1 = (n - b1) % m
    while (b1 < BigInteger.ZERO) b1 += m
    var c = BigInteger.ZERO
    while ((c * a1) % m != b1) c++
    b = c * a + b
    a = m * a
  }
  return b
}