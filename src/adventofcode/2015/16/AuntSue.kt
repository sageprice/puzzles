package adventofcode.`2015`.`16`

import java.io.File

fun main() {
  val sues = File("src/adventofcode/2015/16/input.txt").readLines().map { parseSue(it) }
  val theRealSue = """
    children: 3
    cats: 7
    samoyeds: 2
    pomeranians: 3
    akitas: 0
    vizslas: 0
    goldfish: 5
    trees: 3
    cars: 2
    perfumes: 1
  """.trimIndent().split("\n").map {
    val (thing, count) = it.split(": ")
    thing to count.toInt()
  }.toMap()

  println(sues.first { sue ->
    sue.things.entries.all { (k, v) -> theRealSue[k]!! == v }
  }.number)

  println(sues.first { sue ->
    sue.things.entries.all { (k, v) ->
      when (k) {
        "cats", "trees" -> theRealSue[k]!! < v
        "pomeranians", "goldfish" -> theRealSue[k]!! > v
        else -> theRealSue[k]!! == v
      }
    }
  }.number)
}


// Sue 7: trees: 2, samoyeds: 7, goldfish: 10
private val SUE_REGEX = Regex(
  "Sue (\\d+): ([a-z]+): (\\d+), ([a-z]+): (\\d+), ([a-z]+): (\\d+)"
)

private fun parseSue(line: String): Sue {
  val groups = SUE_REGEX.matchEntire(line)!!.groupValues
  return Sue(
    number = groups[1].toInt(),
    mapOf(
      groups[2] to groups[3].toInt(),
      groups[4] to groups[5].toInt(),
      groups[6] to groups[7].toInt(),
    )
  )
}

data class Sue(
  val number: Int,
  val things: Map<String, Int>
)


