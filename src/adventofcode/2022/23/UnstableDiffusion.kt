package adventofcode.`2022`.`23`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/23/input.txt").readLines()

  // Part 1
  var elves = parseInitialElfLocations(input)
  val lookOrder = Direction.values().toMutableList()
  var minX = elves.minOf { it.x }
  var minY = elves.minOf { it.y }
  var maxX = elves.maxOf { it.x }
  var maxY = elves.maxOf { it.y }
  repeat(10) {
    elves = moveElves(elves, lookOrder)
    lookOrder.add(lookOrder.removeFirst())
    minX = elves.minOf { it.x }
    minY = elves.minOf { it.y }
    maxX = elves.maxOf { it.x }
    maxY = elves.maxOf { it.y }
  }
  var c = 0
  for (x in minX..maxX) for (y in minY..maxY) if (!elves.contains(Elf(x, y))) c++
  println(c)

  // Part 2
  var oldElves = setOf<Elf>()
  var rounds = 10
  while (oldElves != elves) {
    rounds++
    oldElves = elves
    elves = moveElves(elves, lookOrder)
    lookOrder.add(lookOrder.removeFirst())
  }
  println(rounds)
}

private data class Elf(val x: Int, val y: Int)

private enum class Direction {
  NORTH, SOUTH, WEST, EAST
}

private fun parseInitialElfLocations(input: List<String>): Set<Elf> {
  val elves = mutableSetOf<Elf>()
  for (i in input.indices) for (j in 0 until input[i].length) if (input[i][j] == '#') elves.add(Elf(j, i))
  return elves
}

private fun moveElves(elves: Set<Elf>, directions: List<Direction>): Set<Elf> {
  val decisions = mutableMapOf<Elf, MutableList<Elf>>()
  for (elf in elves) {
    if (elfStays(elf, elves)) {
      decisions[elf] = mutableListOf(elf)
      continue
    }
    val (x, y) = elf
    var moved = false
    for (d in directions) {
      when (d) {
        Direction.NORTH -> {
          if (Elf(x-1, y-1) !in elves && Elf(x, y-1) !in elves && Elf(x+1, y-1) !in elves) {
            moved = true
            val newLocation = Elf(x, y-1)
            if (decisions.containsKey(newLocation)) {
              decisions[newLocation]!!.add(elf)
            } else decisions[newLocation] = mutableListOf(elf)
            break
          }
        }
        Direction.SOUTH -> {
          if (Elf(x-1, y+1) !in elves && Elf(x, y+1) !in elves && Elf(x+1, y+1) !in elves) {
            moved = true
            val newLocation = Elf(x, y+1)
            if (decisions.containsKey(newLocation)) {
              decisions[newLocation]!!.add(elf)
            } else decisions[newLocation] = mutableListOf(elf)
            break
          }
        }
        Direction.WEST -> {
          if (Elf(x-1, y-1) !in elves && Elf(x-1, y) !in elves && Elf(x-1, y+1) !in elves) {
            moved = true
            val newLocation = Elf(x-1, y)
            if (decisions.containsKey(newLocation)) {
              decisions[newLocation]!!.add(elf)
            } else decisions[newLocation] = mutableListOf(elf)
            break
          }
        }
        Direction.EAST -> {
          if (Elf(x+1, y-1) !in elves && Elf(x+1, y) !in elves && Elf(x+1, y+1) !in elves) {
            moved = true
            val newLocation = Elf(x+1, y)
            if (decisions.containsKey(newLocation)) {
              decisions[newLocation]!!.add(elf)
            } else decisions[newLocation] = mutableListOf(elf)
            break
          }
        }
      }
    }
    if (!moved) decisions[elf] = mutableListOf(elf)
  }
  val newLocations = mutableSetOf<Elf>()
  for ((elf, oldLocations) in decisions) {
    if (oldLocations.size == 1)
      newLocations.add(elf)
    else newLocations.addAll(oldLocations)
  }
  return newLocations
}

private fun elfStays(e: Elf, elves: Set<Elf>): Boolean {
  val (x, y) = e
  for (dx in -1..1) for (dy in -1..1) {
    if (dx == 0 && dy == 0) continue
    if (elves.contains(Elf(x+dx, y+dy))) return false
  }
  return true
}

//private fun printGround(elves: Set<Elf>, xs: IntRange, ys: IntRange) {
//  println("========================================")
//  for (y in ys) {
//    for (x in xs) {
//      if (Elf(x, y) in elves) {
//        print("#")
//      } else print(".")
//    }
//    println()
//  }
//}