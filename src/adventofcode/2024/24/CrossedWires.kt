package adventofcode.`2024`.`24`

import adventofcode.`2024`.`24`.Operator.*
import java.io.File

/** https://adventofcode.com/2024/day/24 */
fun main() {
  val (inputLines, gateLines) = File("src/adventofcode/2024/24/input.txt").readText().split("\n\n")
  val inputs = inputLines.split("\n").map { it.split(": ") }.associate { (gate, init) -> gate to init.toInt() }
  val gates = gateLines.split("\n").map { it.split(" ") }.map { chunks ->
    val l = chunks[0]
    val r = chunks[2]
    val result = chunks.last()
    val op = Operator.valueOf(chunks[1])
    Operation(l, r, result, op)
  }.toSet()
  // Part 1
  val outputs = simulateExecution(inputs, gates)
  val result = outputs!!.toList().filter { it.first.startsWith("z") }.sortedByDescending { it.first }.map { it.second }.joinToString("").toLong(2)
  println(result)

  // Part 2
  val xIn = inputs.filter { it.key.startsWith("x") }.toList().sortedByDescending { it.first }.map { it.second }.joinToString("").toLong(2)
  val yIn = inputs.filter { it.key.startsWith("y") }.toList().sortedByDescending { it.first }.map { it.second }.joinToString("").toLong(2)
  println("x       :  " + xIn.toString(2))
  println("y       :  " + yIn.toString(2))
  val expectedZ = (xIn + yIn).toString(2)
  println("Expected: " + expectedZ)
  val actualZ = result.toString(2)
  println("Actual:   " + actualZ)

  // Swapped: z07, swt
  val fixes = fixFlips(inputs, gates)
  println(fixes.flatMap { listOf(it.first, it.second) }.sorted().joinToString(","))
}

private fun fixFlips(inputs: Map<String, Int>, initialGates: Set<Operation>): List<Pair<String, String>> {
  val xs = inputs.keys.filter { it.startsWith("x") }
  val ys = inputs.keys.filter { it.startsWith("y") }
  val zs = initialGates.map { it.output }.filter { it.startsWith("z") }.sorted()
  val allZeros = mutableMapOf<String, Int>()
  val allOnes = mutableMapOf<String, Int>()
  val allXOnes = mutableMapOf<String, Int>()
  val allYOnes = mutableMapOf<String, Int>()
  val altXStart0 = mutableMapOf<String, Int>()
  val altYStart0 = mutableMapOf<String, Int>()
  for (x in xs) {
    allZeros[x] = 0
    allOnes[x] = 1
    allXOnes[x] = 1
    allYOnes[x] = 0
    altXStart0[x] = if (x.last().toString().toInt() % 2 == 0) 0 else 1
    altYStart0[x] = if (x.last().toString().toInt() % 2 == 0) 1 else 0
  }
  for (y in ys) {
    allZeros[y] = 0
    allOnes[y] = 1
    allXOnes[y] = 0
    allYOnes[y] = 1
    altXStart0[y] = if (y.last().toString().toInt() % 2 == 0) 1 else 0
    altYStart0[y] = if (y.last().toString().toInt() % 2 == 0) 0 else 1
  }
  for (z in zs) {
    allZeros[z] = 0
    allOnes[z] = if (z == "z00") 0 else 1
    allXOnes[z] = if (z.endsWith("45")) 0 else 1
    allYOnes[z] = if (z.endsWith("45")) 0 else 1
    altXStart0[z] = if (z.endsWith("45")) 0 else 1
    altYStart0[z] = if (z.endsWith("45")) 0 else 1
  }
  val tests = listOf(allZeros, allOnes, allXOnes, allYOnes, altXStart0, altYStart0)
  return fixNetwork(inputs, initialGates, tests) ?: throw IllegalStateException("Couldn't find solution")
}

private fun fixNetwork(
  inputs: Map<String, Int>,
  initialGates: Set<Operation>,
  tests: List<Map<String, Int>>,
  iStart: Int = 2
): List<Pair<String, String>>? {
  val maxZ = 45
  val gates = initialGates.toMutableSet()
  for (i in iStart .. maxZ) {
    val priorStop = if (i-1 < 10) "z0${i-1}" else "z${i-1}"
    val prior = simulateExecution(inputs.filter { it.key.substring(1).toInt() <= i-1 }, gates) ?: throw IllegalStateException("Invalid state")
    val currStop = if (i < 10) "z0${i}" else "z${i}"
    val curr = simulateExecution(inputs.filter { it.key.substring(1).toInt() <= i }, gates) ?: throw IllegalStateException("Invalid state")
    val swapCandidates = curr.keys.filter { it !in prior && !(it.startsWith("y") || it.startsWith("x")) }.toMutableList()
    if (swapCandidates.isEmpty()) {
      continue
    }
    if (!tests.all { testPasses(inputs, gates, it, stopper = currStop) }) {
      swapCandidates.add(priorStop)
      val swaps = getFixSwaps(inputs, gates.toMutableSet(), currStop, tests, swapCandidates)
      for (swapSet in swaps) {
        for ((a, b) in swapSet) {
          val aOut = gates.firstOrNull { it.output == a }
          val bOut = gates.firstOrNull { it.output == b }
          if (aOut == null || bOut == null) continue
          gates.remove(aOut)
          gates.remove(bOut)
          val newAOut = Operation(aOut.left, aOut.right, bOut.output, aOut.op)
          gates.add(newAOut)
          val newBOut = Operation(bOut.left, bOut.right, aOut.output, bOut.op)
          gates.add(newBOut)
        }
        val fixed = fixNetwork(inputs, gates, tests, i+1)
        if (fixed != null) {
          println("Good contribution at $i: ${swapSet.flatMap { listOf(it.first, it.second) }}")
          return fixed + swapSet
        }
        for ((a, b) in swapSet) {
          val aOut = gates.firstOrNull { it.output == a }
          val bOut = gates.firstOrNull { it.output == b }
          if (aOut == null || bOut == null) {
            continue
          }
          gates.remove(aOut)
          gates.remove(bOut)
          val newAOut = Operation(aOut.left, aOut.right, bOut.output, aOut.op)
          gates.add(newAOut)
          val newBOut = Operation(bOut.left, bOut.right, aOut.output, bOut.op)
          gates.add(newBOut)
        }
      }
      return null
    }
  }
  if (tests.all { testPasses(inputs, gates, it, stopper = "z45") }) {
    /*
    // The result with all the swapped gates.
    println("Final value: " + getBinary("z", simulateExecution(inputs, gates)!!))

    // Log for manual inspection - copy this chunk into graphviz to see the graph.
    println("===============")
    for (gate in gates) {
      val arrowhead = when (gate.op) {
        XOR -> "diamond"
        AND -> "normal"
        OR -> "dot"
      }
      println("  ${gate.left} -> ${gate.output} [arrowhead=\"$arrowhead\"]")
      println("  ${gate.right} -> ${gate.output} [arrowhead=\"$arrowhead\"]")
    }
    println("===============")
    */
    return emptyList()
  }
  return null
}

private fun getFixSwaps(
  inputs: Map<String, Int>,
  gates: MutableSet<Operation>,
  currStop: String,
  tests: List<Map<String, Int>>,
  swapCandidates: List<String>
): List<List<Pair<String, String>>> {
  val swapCombos = getPotentialSwaps(swapCandidates)
  val options = mutableListOf<List<Pair<String, String>>>()
  for (combo in swapCombos) {
    // This one doesn't work - verified through manual inspection.
    if ("rgc" in combo.flatMap { listOf(it.first, it.second) }) continue
    for ((a, b) in combo) {
      val aOut = gates.firstOrNull { it.output == a }
      val bOut = gates.firstOrNull { it.output == b }
      if (aOut == null || bOut == null) continue
      gates.remove(aOut)
      gates.remove(bOut)
      val newAOut = Operation(aOut.left, aOut.right, bOut.output, aOut.op)
      gates.add(newAOut)
      val newBOut = Operation(bOut.left, bOut.right, aOut.output, bOut.op)
      gates.add(newBOut)
    }
    if (tests.all { testPasses(inputs, gates, it, stopper = currStop) }) {
      options.add(combo)
    }
    // Flip it back
    for ((a, b) in combo) {
      val aOut = gates.firstOrNull { it.output == a }
      val bOut = gates.firstOrNull { it.output == b }
      if (aOut == null || bOut == null) {
        continue
      }
      gates.remove(aOut)
      gates.remove(bOut)
      val newAOut = Operation(aOut.left, aOut.right, bOut.output, aOut.op)
      gates.add(newAOut)
      val newBOut = Operation(bOut.left, bOut.right, aOut.output, bOut.op)
      gates.add(newBOut)
    }
  }
  return options
}

private fun getPotentialSwaps(swapCandidates: List<String>): List<List<Pair<String, String>>> {
  val singleSwaps = mutableListOf<Pair<String, String>>()
  for (i in swapCandidates.indices) for (j in i+1 until swapCandidates.size) {
    singleSwaps.add(swapCandidates[i] to swapCandidates[j])
  }
  return getAllCombinations(singleSwaps).filter { it.isNotEmpty() }.filter { l ->
    val entries = l.flatMap { listOf(it.first, it.second) }
    entries.size == entries.distinct().size
  }
}

private fun <T> getAllCombinations(ts: List<Pair<T, T>>): List<List<Pair<T, T>>> {
  if (ts.isEmpty()) return listOf(emptyList())
  val combos = getAllCombinations(ts.subList(1, ts.size)).filter { it.size <= 3 }
  return combos + combos.map { it + ts.first() }
}

private fun getBinary(ch: String, map: Map<String, Int>): String {
  return map.toList().filter { it.first.startsWith(ch) }.sortedByDescending { it.first }.map { it.second }.joinToString("")
}

private fun testPasses(
  inputs: Map<String, Int>,
  initialGates: Set<Operation>,
  test: Map<String, Int>,
  stopper: String): Boolean {
  val testIn = inputs.toMutableMap()
  for (t in test.keys) {
    if (t in testIn) {
      testIn[t] = test[t] ?: throw IllegalArgumentException("Cannot find key $t in test input: $test")
    }
  }
  val results = simulateExecution(testIn, initialGates) ?: return false
  for (k in test.keys) {
    if (k > stopper) continue
    if (results[k] != test[k]) {
      return false
    }
  }
  return true
}

private fun simulateExecution(inputs: Map<String, Int>, initialGates: Set<Operation>): Map<String, Int>? {
  val outputs = inputs.toMutableMap()
  val gates = initialGates.toMutableSet()
  while (gates.isNotEmpty()) {
    val processedGates = mutableSetOf<Operation>()
    for (gate in gates) {
      val l = outputs[gate.left]
      val r = outputs[gate.right]
      if (l != null && r != null) {
        val (name, value) = gate.execute(l, r)
        outputs[name] = value
        processedGates.add(gate)
      }
    }
    if (processedGates.isEmpty()) return outputs
    gates.removeAll(processedGates)
  }
  return outputs
}

private data class Operation(val left: String, val right: String, val output: String, val op: Operator) {
  fun execute(l: Int, r: Int): Pair<String, Int> {
    return output to when (op) {
      XOR -> l.xor(r)
      AND -> l.and(r)
      OR -> l.or(r)
    }
  }
}

private enum class Operator {
  XOR, AND, OR
}
