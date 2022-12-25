package adventofcode.`2022`.`25`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/25/input.txt").readLines()
  val t = input.sumOf { getFuelCost(it) }
  println(encodeSnafuNumber(t))
}

private fun getFuelCost(s: String): Long {
  val ds = s.map { c ->
    when (c) {
      '2' -> 2
      '1' -> 1
      '0' -> 0
      '-' -> -1
      '=' -> -2
      else -> error("I don't know this character $c in $s")
    }
  }
  var t = 0L
  for (d in ds) t = 5*t + d
  return t
}

private fun encodeSnafuNumber(x: Long): String {
  val b5ds = x.toString(5).reversed().map { it.digitToInt() }.toMutableList()
  val encoded = mutableListOf<String>()
  var tail: Int? = null
  b5ds.forEachIndexed { idx, i ->
    if (i in 0..2) encoded.add(i.toString())
    else when (i) {
      3 -> {
        if (idx+1 in b5ds.indices) b5ds[idx + 1]++ else tail = 1
        encoded.add("=")
      }
      4 -> {
        if (idx+1 in b5ds.indices) b5ds[idx + 1]++ else tail = 1
        encoded.add("-")
      }
      5 -> {
        if (idx+1 in b5ds.indices) b5ds[idx + 1]++ else tail = 1
        encoded.add("0")
      }
      else -> error("Digit [$i] seems too large from $b5ds.")
    }
  }
  if (tail != null) encoded.add(tail.toString())
  return encoded.reversed().joinToString("")
}
