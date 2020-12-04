package adventofcode.`2020`.`04`

import java.io.File

fun main() {
  val lines: List<String> =
    File("src/adventofcode/2020/04/input.txt")
      .readLines()

  var i = 0
  var components = mutableMapOf<String, String>()
  val passports = mutableListOf<Map<String, String>>()
  while (i < lines.size) {
    if (lines[i].isEmpty()) {
      passports.add(components)
      components = mutableMapOf()
    } else {
      lines[i].split(" ").map { it.split(":") }.forEach { components[it[0]] = it[1] }
    }
    i++
  }
  // last set of data
  passports.add(components)

  val sortaValidPassports = passports.filter { it.size == 8 || it.size == 7 && !it.containsKey("cid") }
  // Part 1
  println(sortaValidPassports.size)
  // Part 2
  val checkedPws = sortaValidPassports
    .asSequence()
    .filter { it["byr"]!!.toInt() in 1920..2002 }
    .filter { it["iyr"]!!.toInt() in 2010..2020 }
    .filter { it["eyr"]!!.toInt() in 2020..2030 }
    .filter {
      val hgt = it["hgt"] ?: error("No hgt found: $it")
      val units = hgt.substring(hgt.length - 2)
      val height = hgt.substring(0, hgt.length - 2).toInt()
      if (units == "cm") {
        height in 150..193
      } else {
        height in 59..76
      }
    }
    .filter {
      val hcl = it["hcl"]!!
      hcl[0] == '#' && hcl.substring(1).all { c -> "0123456789abcdef".contains(c) }
    }
    .filter {
      setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(it["ecl"]!!)
    }
    .filter {
      it["pid"]!!.length == 9 && it["pid"]!!.all { c -> "0123456789".contains(c) }
    }
    .toList()
  println(checkedPws.size)
}