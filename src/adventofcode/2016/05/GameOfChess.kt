package adventofcode.`2016`.`05`

import java.io.File
import java.security.MessageDigest

fun main() {
  val root = File("src/adventofcode/2016/05/input.txt").readText()

  val md5 = MessageDigest.getInstance("MD5")
  var pw = ""
  val targetLength = 8
  for (i in 1L..500_000_000) {
    val seed = "$root$i"
    val hashed = md5.digest(seed.toByteArray())
    val hex = bytesToHex(hashed)
    if (hex.substring(0, 5).all { it == '0' }) {
      pw += hex[5]
      if (pw.length == targetLength) {
        break
      }
    }
  }
  println(pw)

  val pw2 = Array(8){ "" }
  val unseen = mutableSetOf(0, 1, 2, 3, 4, 5, 6, 7)
  var i = 0
  val digits = unseen.map { it.toString() }.toSet()
  while (unseen.isNotEmpty()) {
    ++i
    val seed = "$root$i"
    val hashed = md5.digest(seed.toByteArray())
    val hex = bytesToHex(hashed)
    if (hex.substring(0, 5).all { it == '0' }) {
      if (hex.substring(5, 6) !in digits) continue
        val position = hex.substring(5, 6).toInt()
      if (position in unseen) {
        unseen.remove(position)
        pw2[position] = hex.substring(6, 7)
      }
    }
  }
  println(pw2.joinToString(""))
}

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
fun bytesToHex(bytes: ByteArray): String {
  val hexChars = CharArray(bytes.size * 2)
  for (j in bytes.indices) {
    val v = bytes[j].toInt() and 0xFF
    hexChars[j * 2] = HEX_ARRAY[v ushr 4]
    hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
  }
  return String(hexChars)
}