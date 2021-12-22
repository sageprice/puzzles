package adventofcode.`2020`.`23`

fun main() {
  val input = "326519478".split("").filter { it.isNotBlank() }.map { it.toInt() }

  val cups = simulateMapGame(input, 100)
  var s = cups[1]
  var answer = ""
  while (s != 1) {
    answer += s
    s = cups[s]!!
  }
  println("Part 1: $answer")

  // Part 2
  val game2Out = simulateMapGame(input + ((input.maxOrNull()!! + 1) .. 1_000_000), 10_000_000)
  val first = game2Out[1]!!
  val second = game2Out[first]!!
  println("Part 2: ${first.toLong() * second.toLong()}")
}

private fun simulateMapGame(cups: List<Int>, steps: Int): Map<Int, Int> {
  // Didn't feel like writing a linked list, so I'm abusing a map to act as one.
  val maxCup = cups.maxOrNull()!!
  val gameState = mutableMapOf<Int, Int>()
  (0 until cups.size-1).forEach { gameState[cups[it]] = cups[it+1] }
  gameState[cups.last()] = cups.first()
  var head = cups.first()
  repeat(steps) {
    // Grab the three values to move.
    val ntStart = gameState[head]!!
    val ntMiddle = gameState[ntStart]!!
    val ntEnd = gameState[ntMiddle]!!
    val nextThree = listOf(ntStart, ntMiddle, ntEnd)
    // Close the loop, remove the three to move.
    gameState[head] = gameState[ntEnd]!!
    nextThree.forEach { gameState.remove(it) }
    val nextHead = gameState[head]!!
    // Find location to insert the three, then add them.
    val dest = getDestination(head, maxCup, gameState.keys)
    gameState[ntEnd] = gameState[dest]!!
    gameState[ntMiddle] = ntEnd
    gameState[ntStart] = ntMiddle
    gameState[dest] = ntStart
    // Update the head
    head = nextHead
  }
  return gameState
}

private fun getDestination(x: Int, maxValue: Int, cups: Set<Int>): Int {
  var dest = x-1
  while (dest !in 1..maxValue || !cups.contains(dest)) {
    if (dest < 1) dest = maxValue
    else dest--
  }
  return dest
}
