package adventofcode.`2015`.`10`

fun main() {
  val input = "3113322113"
  var said = input
  repeat(40) { said = say(said) }
  println("Part 1: ${said.length}")
  repeat(10) { said = say(said) }
  println("Part 2: ${said.length}")
}

fun say(str: String): String {
  val output = StringBuilder()
  var previous: Char? = null
  var length = 0
  for (i in str.indices) {
    if (previous == str[i]) {
      length++
    } else {
      if (previous != null) output.append(length).append(previous)
      previous = str[i]
      length = 1
    }
  }
  return output.append(length).append(previous).toString()
}