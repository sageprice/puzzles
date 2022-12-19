package adventofcode.`2022`.`19`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val input = File("src/adventofcode/2022/19/input.txt")
    .readLines()
    .map { blueprint -> parseRobotSpecs(blueprint) }

  // Part 1
  println(input.sumOf { it.blueprintNum * maxCrackedGeodes(it, 24) })

  // Part 2
  println(input.subList(0, min(input.size, 3)).map { maxCrackedGeodes(it, 32) }.reduce { a, b -> a * b })
}

private data class RobotsSpec(
  val blueprintNum: Int,
  val oreRobotCost: Int,
  val clayRobotCost: Int,
  val obsidianRobotCost: Pair<Int, Int>,
  val geodeRobotCost: Pair<Int, Int>
)

private data class HarvestState(
  val ores: Int,
  val clays: Int,
  val obsidians: Int,
  val geodes: Int,
  val oreRobots: Int,
  val clayRobots: Int,
  val obsidianRobots: Int,
  val geodeRobots: Int
)

private fun maxCrackedGeodes(spec: RobotsSpec, t: Int): Int {
  cache.clear()
  best = 0
  val initialState = HarvestState(2, 0, 0, 0, 1, 0, 0, 0)
  // Precompute so we don't have to make a new list in each loop.
  val maxOres = listOf(spec.oreRobotCost, spec.clayRobotCost, spec.obsidianRobotCost.first, spec.geodeRobotCost.first).max()
  return crackGeodes(spec, initialState, t-2, maxOres)
}

// oh no global state how dare he
private val cache = mutableMapOf<Pair<HarvestState, Int>, Int>()
private var best = 0

private fun crackGeodes(spec: RobotsSpec, state: HarvestState, t: Int, maxOres: Int): Int {
  // We're at the bottom.
  if (t == 0) return state.geodes

  // Even if we make a geode robot every turn we can't catch up, so just bail out early.
  if (state.geodeRobots * t + t*(t-1) / 2 + state.geodes < best) return 0

  // Caching helps a lot with runtime.
  if (cache.containsKey(Pair(state, t))) return cache[Pair(state, t)]!!

  val newOre = state.oreRobots
  val newClay = state.clayRobots
  val newObsidian = state.obsidianRobots
  val newGeodes = state.geodeRobots

  // Heuristics: we don't need to make a robot if we make enough of a resource per turn to make any robot.
  var maxGeodes = 0
  if (state.ores >= spec.geodeRobotCost.first && state.obsidians >= spec.geodeRobotCost.second) {
    val newState = HarvestState(
      state.ores + newOre - spec.geodeRobotCost.first,
      state.clays + newClay,
      state.obsidians + newObsidian - spec.geodeRobotCost.second,
      state.geodes + newGeodes,
      state.oreRobots,
      state.clayRobots,
      state.obsidianRobots,
      state.geodeRobots + 1)
    maxGeodes = max(maxGeodes, crackGeodes(spec, newState, t-1, maxOres))
  }
  if (state.oreRobots < maxOres && state.ores >= spec.oreRobotCost) {
    val newState = HarvestState(
      state.ores + newOre - spec.oreRobotCost,
      state.clays + newClay,
      state.obsidians + newObsidian,
      state.geodes + newGeodes,
      state.oreRobots + 1,
      state.clayRobots,
      state.obsidianRobots,
      state.geodeRobots)
    maxGeodes = max(maxGeodes, crackGeodes(spec, newState, t-1, maxOres))
  }
  if (state.clayRobots < spec.obsidianRobotCost.second && state.ores >= spec.clayRobotCost) {
    val newState = HarvestState(
      state.ores + newOre - spec.clayRobotCost,
      state.clays + newClay,
      state.obsidians + newObsidian,
      state.geodes + newGeodes,
      state.oreRobots,
      state.clayRobots + 1,
      state.obsidianRobots,
      state.geodeRobots)
    maxGeodes = max(maxGeodes, crackGeodes(spec, newState, t-1, maxOres))
  }
  if (state.obsidianRobots < spec.geodeRobotCost.second &&
    state.ores >= spec.obsidianRobotCost.first && state.clays >= spec.obsidianRobotCost.second) {
    val newState = HarvestState(
      state.ores + newOre - spec.obsidianRobotCost.first,
      state.clays + newClay - spec.obsidianRobotCost.second,
      state.obsidians + newObsidian,
      state.geodes + newGeodes,
      state.oreRobots,
      state.clayRobots,
      state.obsidianRobots + 1,
      state.geodeRobots)
    maxGeodes = max(maxGeodes, crackGeodes(spec, newState, t-1, maxOres))
  }
  maxGeodes = max(maxGeodes, crackGeodes(
    spec, HarvestState(
      state.ores + newOre,
      state.clays + newClay,
      state.obsidians + newObsidian,
      state.geodes + newGeodes,
      state.oreRobots,
      state.clayRobots,
      state.obsidianRobots,
      state.geodeRobots
    ), t - 1, maxOres))

  cache[Pair(state, t)] = maxGeodes
  if (maxGeodes > best) best = maxGeodes

  return maxGeodes
}

private fun parseRobotSpecs(blueprint: String): RobotsSpec {
  val (oreRobot, clayRobot, obsidianRobot, geodeRobot) = blueprint.split(". ")
  val (blueprintNum) =
    Regex("Blueprint (\\d+).*").find(blueprint)?.destructured
      ?: error("Failed to parse blueprint num from \"$blueprint\"")
  val (oreRobotCost) =
    Regex(".* costs (\\d).*").find(oreRobot)?.destructured ?: error("Failed to parse oreRobot: \"$blueprint\"")
  val (clayRobotCost) =
    Regex(".* costs (\\d).*").find(clayRobot)?.destructured ?: error("Failed to parse clayRobot: \"$blueprint\"")
  val (obsidianRobotOreCost, obsidianRobotClayCost) =
    Regex(".*(\\d) ore and (\\d+) clay.*").find(obsidianRobot)?.destructured
      ?: error("Failed to parse obsiRobot: \"$blueprint\"")
  val (geodeRobotOreCost, geodeRobotObsidianCost) =
    Regex(".*(\\d) ore and (\\d+) obsidian.*").find(geodeRobot)?.destructured
      ?: error("Failed to parse geodeRobot: \"$blueprint\"")
  return RobotsSpec(
    blueprintNum.toInt(),
    oreRobotCost.toInt(),
    clayRobotCost.toInt(),
    Pair(obsidianRobotOreCost.toInt(), obsidianRobotClayCost.toInt()),
    Pair(geodeRobotOreCost.toInt(), geodeRobotObsidianCost.toInt()))
}
