package adventofcode.`2015`.`19`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val input = File("src/adventofcode/2015/19/input.txt").readText()
  val (lines, molecule) = input.split("\n\n")
  val swaps = lines.split("\n").map {
    val (before, after) = it.split(" => ")
    before to after
  }
  val singleTransformed = transform(swaps, molecule)
  println(singleTransformed.size)

  println(disassembleMolecule(swaps, molecule))
}

private fun transform(swaps: List<Pair<String, String>>, molecule: String): List<String> {
  val newMolecules = mutableListOf<String>()
  swaps.forEach { (before, after) ->
    newMolecules.addAll(makeAllSingleSwaps(before, after, molecule))
  }
  return newMolecules.reversed()
}

private fun makeAllSingleSwaps(before: String, after: String, molecule: String): Set<String> {
  var index = molecule.indexOf(before)
  val newMolecules = mutableSetOf<String>()
  while (index != -1) {
    newMolecules.add(molecule.substring(0, index) + after + molecule.substring(index + before.length))
    index = molecule.indexOf(before, index+1)
  }
  return newMolecules
}

private fun disassembleMolecule(swaps: List<Pair<String, String>>, molecule: String): Int {
  val reversed = swaps.map { it.second to it.first }
  val target = "e"
  val molecules = PriorityQueue(
    Comparator.comparing { p: Pair<String, Int> -> p.first.length }.thenComparing { p -> p.second })
  molecules.add(molecule to 0)
  val seen = mutableMapOf<String, Int>()
  while (molecules.isNotEmpty()) {
    val shortest = molecules.remove()
    if (shortest.first == target) {
      return shortest.second
    }
    if ((seen[shortest.first] ?: Int.MAX_VALUE) <= shortest.second) continue
    seen[shortest.first] = shortest.second
    molecules.addAll(transform(reversed, shortest.first).map { it to shortest.second + 1 })
  }
  throw IllegalStateException("Never found a path :(")
}
