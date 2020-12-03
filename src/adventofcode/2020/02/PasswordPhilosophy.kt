package adventofcode.`2020`.`02`

import java.io.File

fun main() {
  val passwords: List<PasswordConfig> =
    File("src/adventofcode/2020/02/input.txt")
      .readLines()
      .map { toPassword(it) }

  // Part 1
  println(passwords.count { isValidPass(it) })
  // Part 2
  println(passwords.count { isValidPart2PW(it) })
}

private fun toPassword(str: String): PasswordConfig {
  val components = str.split(" ")
  val countRange = components[0].split("-")
  return PasswordConfig(
    low = countRange[0].toInt(),
    high = countRange[1].toInt(),
    c = components[1][0],
    pw = components[2])
}

private data class PasswordConfig(val low: Int, val high: Int, val c: Char, val pw: String)

private fun isValidPass(pc: PasswordConfig): Boolean {
  val requiredCharCount = pc.pw.count { it == pc.c }
  return requiredCharCount <= pc.high && requiredCharCount >= pc.low
}

private fun isValidPart2PW(pc: PasswordConfig): Boolean {
  val isInFirstSlot = pc.pw.length >= pc.low && pc.pw[pc.low-1] == pc.c
  val isInSecondSlot = pc.pw.length >= pc.high && pc.pw[pc.high-1] == pc.c
  return isInFirstSlot.xor(isInSecondSlot)
}