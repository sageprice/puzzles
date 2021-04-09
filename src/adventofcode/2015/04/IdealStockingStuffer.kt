package adventofcode.`2015`.`04`

import java.math.BigInteger
import java.security.MessageDigest

fun main() {
  val input = "bgvyzdsv"

  println("Part 1: " + findPrefixedHash(input, "00000"))
  println("Part 2: " + findPrefixedHash(input, "000000"))
}

private fun findPrefixedHash(input: String, prefix: String): Int {
  var i = 0
  while (true) {
    i++
    val hash = md5(input + i)
    if (hash.startsWith(prefix)) return i
  }
}

/** https://stackoverflow.com/a/64171625 */
private fun md5(input: String): String {
  val md = MessageDigest.getInstance("MD5")
  return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
}