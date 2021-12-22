package adventofcode.`2020`.`09`

import java.io.File

fun main() {
  val numbers =
      File("src/adventofcode/2020/09/input.txt")
          .readLines()
          .map { it.toLong() }

  // Part 1
  val offValue = findBreak(numbers)
  println("Part 1: $offValue")

  // Part 2
  println("Part 2: " + findSumRange(numbers, offValue))
}

private fun findBreak(nums: List<Long>): Long {
  val recentSet = nums.subList(0, 25).toMutableSet()
  val recentQueue = nums.subList(0, 25).toMutableList()
  for (i in 25 until nums.size) {
    val x = nums[i]
    var isReachable = false
    for (n in recentQueue) {
      if (recentSet.contains(x-n)) {
        isReachable = true
        break
      }
    }
    if (!isReachable) {
      return x
    }
    val drop = recentQueue.removeAt(0)
    recentSet.remove(drop)
    recentQueue.add(x)
    recentSet.add(x)
  }
  error("Could not find invalid value")
}

private fun findSumRange(nums: List<Long>, target: Long): Long {
  val runningSums = mutableListOf<Long>(nums[0])
  for (i in 1 until nums.size) {
    runningSums.add(runningSums[i-1] + nums[i])
    for (j in 0 until i) {
      if (runningSums[i] - runningSums[j] == target) {
        return nums.subList(j+1, i+1).minOrNull()!! + nums.subList(j+1, i+1).minOrNull()!!
      }
    }
  }
  error("No valid range found")
}