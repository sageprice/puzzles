package adventofcode.`2015`.`11`

fun main() {
  var pw = "hxbxwxba"

  while (!isGoodPassword(pw)) pw = getNextPassword(pw)
  println(pw)

  pw = getNextPassword(pw)
  while (!isGoodPassword(pw)) pw = getNextPassword(pw)
  println(pw)
}

fun getNextPassword(str: String): String {
  return if (str.last() == 'z') {
    getNextPassword(str.substring(0, str.length - 1)) + 'a'
  } else {
    str.substring(0, str.length-1) + (str.last() + 1)
  }
}

fun isGoodPassword(str: String): Boolean {
  return hasGoodLetters(str) && hasIncreasingTriple(str) && hasDoubleDouble(str)
}

fun hasGoodLetters(str: String): Boolean {
  return str.all { it != 'i' && it != 'o' && it != 'l' }
}

fun hasIncreasingTriple(str: String): Boolean {
  for (i in 2 until str.length) {
    if (str[i] == str[i-1] + 1 && str[i-1] == str[i-2] + 1) return true
  }
  return false
}

fun hasDoubleDouble(str: String): Boolean {
  var repeat: Char? = null
  for (i in 1 until str.length) {
    if (str[i] == str[i-1]) {
      if (repeat == null) {
        repeat = str[i]
      }
      else if (str[i] != repeat) return true
    }
  }
  return false
}