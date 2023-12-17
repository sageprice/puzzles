package adventofcode.`2023`.`17`

import java.io.File
import java.util.PriorityQueue

fun main() {
  val city =
    File("src/adventofcode/2023/17/input.txt")
      .readLines()
      .map { it.toCharArray().map { c -> c.toString().toInt() } }

  println(
    getMinCostPath(
      { c -> c.getNext(city) },
      { c -> c.r == city.size-1 && c.c == city.first().size-1 } ))

  println(
    getMinCostPath(
      { c -> c.getNextUltra(city) },
      { c -> c.r == city.size-1 && c.c == city.first().size-1 && c.moves >= 3 } ))
}

private fun getMinCostPath(
  nextFn: (Crucible) -> List<Crucible>,
  isValidEnd: (Crucible) -> Boolean): Int {
  // Use a heap since we're trying to find the minimum path.
  val crucibles = PriorityQueue<Crucible>(Comparator.comparing { c -> c.cost })
  crucibles.add(Crucible(0, 0, 0, 1, -1, 0))
  // Cache so we can skip more expensive returns to already visited places.
  val seen = mutableMapOf<FreeCrucible, Int>()
  while (crucibles.isNotEmpty()) {
    val curr = crucibles.poll()
    if (isValidEnd(curr)) return curr.cost
    val seenCost = seen[curr.toFree()]
    if (seenCost != null && seenCost <= curr.cost) continue
    seen[curr.toFree()] = curr.cost
    crucibles.addAll(nextFn(curr))
  }
  error("ran out of options")
}

private fun Crucible.getNextUltra(city: List<List<Int>>): List<Crucible> {
  val next = mutableListOf<Crucible>()
  if (moves < 9) this.goStraight(city)?.also { next.add(it) }
  if (moves >= 3) {
    this.turnLeft(city)?.also { next.add(it) }
    this.turnRight(city)?.also { next.add(it) }
  }
  return next
}

private fun Crucible.getNext(city: List<List<Int>>): List<Crucible> {
  return listOfNotNull(this.goStraight(city), this.turnLeft(city), this.turnRight(city)).filter { it.moves < 3 }
}

private fun Crucible.goStraight(city: List<List<Int>>): Crucible? =
  if (r + dr in city.indices && c + dc in city.first().indices)
    Crucible (r + dr, c + dc, dr, dc, moves + 1, cost + city[r+dr][c+dc])
  else null
private fun Crucible.turnLeft(city: List<List<Int>>): Crucible? =
  if (r - dc in city.indices && c + dr in city.first().indices)
    Crucible (r - dc, c + dr, -dc, dr, 0, cost + city[r-dc][c+dr])
  else null
private fun Crucible.turnRight(city: List<List<Int>>): Crucible? =
  if (r + dc in city.indices && c - dr in city.first().indices)
    Crucible (r + dc, c - dr, dc, -dr, 0, cost + city[r+dc][c-dr])
  else null

private fun Crucible.toFree(): FreeCrucible {
  return FreeCrucible(r, c, dr, dc, moves)
}

// Used for min cost caching
private data class FreeCrucible(
  val r: Int,
  val c: Int,
  val dr: Int,
  val dc: Int,
  val moves: Int)

private data class Crucible(
  val r: Int,
  val c: Int,
  val dr: Int,
  val dc: Int,
  val moves: Int,
  val cost: Int)
