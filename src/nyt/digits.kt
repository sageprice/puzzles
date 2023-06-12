package nyt

fun main() {
  val answer = solve(null, listOf(3, 5, 7, 20, 23,25), 494)
  answer?.forEach { println(it) }
}

private fun solve(current: Int?, inputs: List<Int>, target: Int): List<Operation>? {
  if (inputs.isEmpty()) {
    return if (current == target) listOf(End(target)) else null
  }
  if (current == null) {
    val filteredInputs = inputs.toMutableList()
    for (x in inputs.distinct()) {
      filteredInputs.remove(x)
      val ans = solve(x, filteredInputs, target)
      if (ans != null) {
        return (ans + Start(x)).reversed()
      }
      filteredInputs.add(x)
    }
  } else {
    val filteredInputs = inputs.toMutableList()
    for (x in inputs) {
      filteredInputs.remove(x)
      val added = solve(current + x, filteredInputs, target)
      if (added != null) {
        return added + Arithmetic("+", x)
      }
      val subtracted = solve(current - x, filteredInputs, target)
      if (subtracted != null) {
        return subtracted + Arithmetic("-", x)
      }
      val multiplied = solve(current * x, filteredInputs, target)
      if (multiplied != null) {
        return multiplied + Arithmetic("*", x)
      }
      if (current % x == 0) {
        val divided = solve(current / x, filteredInputs, target)
        if (divided != null) {
          return divided + Arithmetic("/", x)
        }
      }
      filteredInputs.add(x)
    }
  }
  return null
}

private sealed class Operation
private data class Start(val x: Int): Operation()
private data class Arithmetic(val op: String, val y: Int): Operation()
private data class End(val end: Int): Operation()