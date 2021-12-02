import java.io.File

fun main() {
  val depths: List<Int> =
    File("src/adventofcode/2021/01/input.txt")
      .readLines()
      .map { it.toInt() }

  var c = 0
  for (i in 1 until depths.size) {
    if (depths[i] > depths[i-1]) c++
  }
  println(c)
  c = 0
  for (i in 3 until depths.size) {
    if (depths[i] > depths[i-3]) c++
  }
  println(c)
}