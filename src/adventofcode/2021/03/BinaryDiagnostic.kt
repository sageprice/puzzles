package adventofcode.`2021`.`03`

import java.io.File

fun main() {

  val binstr: List<String> =
    File("src/adventofcode/2021/03/input.txt")
      .readLines()

  val ones = IntArray(binstr.first().length)
  val zeros = IntArray(binstr.first().length)

  for (b in binstr) {
    for (i in b.indices) {
      if (b[i] == '0') ones[i]++
      else zeros[i]++
    }
  }

  var gamma = 0
  var omega = 0

  for (i in ones.indices) {
    gamma *= 2
    omega *= 2
    if (ones[i] > zeros[i]) gamma++
    else omega++
  }
  println(gamma * omega)

  // Part 2
  var oxyrats = binstr
  var coorats = binstr
  for (i in ones.indices) {
    if (oxyrats.size > 1) {
      val oxy1s = oxyrats.sumBy { if (it[i] == '1') 1 else 0 }
      oxyrats = if (oxy1s >= oxyrats.size - oxy1s) {
        oxyrats.filter { it[i] == '1' }
      } else {
        oxyrats.filter { it[i] == '0' }
      }
    }
    if (coorats.size > 1) {
      val coo1s = coorats.sumBy { if (it[i] == '1') 1 else 0 }
      coorats = if (coo1s >= coorats.size - coo1s) {
        coorats.filter { it[i] == '0' }
      } else {
        coorats.filter { it[i] == '1' }
      }
    }
  }
  var oxyrat = 0
  var coorat = 0

  for (i in ones.indices) {
    oxyrat *= 2
    coorat *= 2
    if (oxyrats.first()[i] == '1') oxyrat++
    if (coorats.first()[i] == '1') coorat++
  }
  println(coorat * oxyrat)
}