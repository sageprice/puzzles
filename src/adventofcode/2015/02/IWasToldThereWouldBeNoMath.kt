package adventofcode.`2015`.`02`

import java.io.File

fun main() {
  val dimensions =
    File("src/adventofcode/2015/02/input.txt")
      .readLines()
      .map { it.split("x").map { n -> n.toInt() } }

  // part 1
  val base = 2L * dimensions.map { ds -> (1L*ds[0]*ds[1] + ds[1]*ds[2] + ds[2]*ds[0]) }.sum()
  val extras = dimensions.map { it.sorted().subList(0,2) }.map { it[0] * it[1] }.sum()
  println(base + extras)

  // part 2
  val wraparounds = dimensions.map { it.sorted().subList(0,2).sum() * 2 }.sum()
  val bows = dimensions.map { it.reduceRight { a, b -> a*b } }.sum()
  println(bows + wraparounds)
}