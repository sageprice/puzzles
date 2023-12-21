package adventofcode.`2023`.`20`

import java.io.File

fun main() {
  val modules = File("src/adventofcode/2023/20/input.txt").readLines().map { parseModule(it) }
  val keyedModules: Map<String, Module> = modules.associateBy { it.name }
  modules.forEach { m ->
    m.receivers.forEach { r ->
      if (keyedModules[r] is Conjunction) (keyedModules[r] as Conjunction).seen[m.name] = false
    }
  }
  // Part 1. My code is stateful, so you have to enable/disable whichever you want to run.
//  println(pushTheButtonRepeatedly(keyedModules, 1000))

  // Part 2
  println(pushUntilRxPulse(keyedModules))
}

private fun printGraphviz() {
  println("digraph Gr {")
  File("src/adventofcode/2023/20/input.txt").readLines().forEach { l ->
    if (l.startsWith("b")) println(l) else {
      if (l.first() == '%') {
        println(l.split(" ").first().substring(1) + " [style=filled, color=green]")
      }
      println(l.substring(1))
    }
  }
  println("}")
}

private fun pushUntilRxPulse(modules: Map<String, Module>): Long {
  var updatedModules = modules
  var pushes = 0
  val prior = mutableMapOf<String, Int>()
  // We need the connections to the final "rx" node to be active at the same time. This
  // means we need the LCM of the cycle lengths of all nodes connected to it[s predecessor].
  val conjunctionRepetends = mutableListOf<Long>()
  do {
    pushes++
    val (_, _, newState, triggered) = pushButton(updatedModules)
    for (c in triggered.distinct()) {
      if (c !in prior) {
        prior[c] = pushes
      } else if (prior[c]!! < 1_000_000) {
        conjunctionRepetends.add(pushes.toLong() - prior[c]!!)
        prior[c] = 1_000_000_000
      }
    }
    updatedModules = newState
  } while (conjunctionRepetends.size < 8) // Magic number based on count of conjunctions in graph.
  return conjunctionRepetends.reduce { a, b -> a*b }
}

private fun pushTheButtonRepeatedly(
  modules: Map<String, Module>, times: Int): Long {
  var lows = 0L
  var highs = 0
  var updatedModules = modules
  repeat(times) {
    val (low, high, newState) = pushButton(updatedModules)
    lows += low
    highs += high
    updatedModules = newState
  }
  return lows * highs
}

private data class ButtonResult(
  val lows: Int, val highs: Int, val modules: Map<String, Module>, val triggered: List<String>)

private fun pushButton(inputModules: Map<String, Module>): ButtonResult {
  val modules = inputModules.toMap()
  val pulses = mutableListOf<Triple<String, String, Boolean>>()
  val broadcaster: Broadcaster = modules["broadcaster"] as Broadcaster
  pulses.addAll(broadcaster.receivers.map { Triple(broadcaster.name, it, false) })
  var lows = 1
  var highs = 0
  val triggeredConjunctions = mutableListOf<String>()
  while (pulses.isNotEmpty()) {
    val (source, receiver, isHigh) = pulses.removeFirst()
    if (isHigh) {
      highs++
    } else lows++
    val recModule = modules[receiver]
    if (recModule is FlipFlop) {
      if (!isHigh) {
        recModule.isOn = !recModule.isOn
        recModule.receivers.forEach { r -> pulses.add(Triple(recModule.name, r, recModule.isOn)) }
      }
    }
    if (recModule is Conjunction) {
      recModule.seen[source] = isHigh
      val isSendingLow = recModule.seen.values.all { it }
      if (isSendingLow) triggeredConjunctions.add(receiver)
      recModule.receivers.forEach { r -> pulses.add(Triple(recModule.name, r, !isSendingLow)) }
    }
  }
  return ButtonResult(lows, highs, modules, triggeredConjunctions)
}

private fun parseModule(input: String): Module {
  val (source, recipients) = input.split(" -> ")
  if (source.first() == '%') {
    return FlipFlop(source.substring(1), recipients.split(", "), false)
  } else if (source.first() == '&') {
    return Conjunction(source.substring(1), recipients.split(", "), mutableMapOf())
  } else if (source == "broadcaster") {
    return Broadcaster(receivers = recipients.split(", "))
  }
  error("Unknown module type: $input")
}

private sealed class Module {
  abstract val name: String
  abstract val receivers: List<String>
}
private data class Broadcaster(
  override val name: String = "broadcaster", override val receivers: List<String>): Module()
private data class FlipFlop(
  override val name: String, override val receivers: List<String>, var isOn: Boolean): Module()
private data class Conjunction(
  override val name: String,
  override val receivers: List<String>,
  val seen: MutableMap<String, Boolean>): Module()
