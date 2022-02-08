package adventofcode.`2015`.`07`

import java.io.File

/**
 * https://adventofcode.com/2015/day/7
 */
fun main() {
  println("Part 1: " +
    execute(
      File("src/adventofcode/2015/07/input.txt").readLines().map { translate(it) })["a"])
  println("Part 2: " +
    execute(
      File("src/adventofcode/2015/07/input2.txt").readLines().map { translate(it) })["a"])
}

fun execute(wires: List<Wire>): Map<String, Int> {
  val unusedWires = wires.toMutableSet()
  val outs = mutableMapOf<String, Int>()
  var wireCount = unusedWires.size
  while (unusedWires.isNotEmpty()) {
    val wiresToRemove = mutableListOf<Wire>()
    for (wire in unusedWires) {
      when (val op = wire.op) {
        is In -> {
          val i = getValue(op.input, outs)
          if (i != null) {
            outs[wire.output] = i
            wiresToRemove.add(wire)
          }
        }
        is Not -> {
          val i = getValue(op.input, outs)
          if (i != null) {
            outs[wire.output] = i.inv()
            wiresToRemove.add(wire)
          }
        }
        is BinaryOp -> {
          val l = getValue(op.left, outs)
          val r = getValue(op.right, outs)
          if (l != null && r != null) {
            when(op.action) {
              "AND" -> outs[wire.output] = l.and(r)
              "OR" -> outs[wire.output] = l.or(r)
              "LSHIFT" -> outs[wire.output] = l.shl(r)
              "RSHIFT" -> outs[wire.output] = l.shr(r)
            }
            wiresToRemove.add(wire)
          }
        }
      }
    }
    unusedWires.removeAll(wiresToRemove)
    if (unusedWires.size == wireCount) error("Did not execute any wires this pass, still have ${unusedWires.size} remaining")
    wireCount = unusedWires.size
  }
  return outs
}
fun getValue(x: String, data: Map<String, Int>): Int? = x.toIntOrNull() ?: data[x]


fun translate(op: String): Wire {
  val (left, right) = op.split(" -> ")
  if (!left.contains(" ")) return Wire(In(left), right)
  val lParts = left.split(" ")
  if (lParts.size == 2) {
    assert(lParts[0] == "NOT")
    return Wire(Not(lParts.last()), right)
  }
  assert(lParts.size == 3)
  return Wire(BinaryOp(lParts[0], lParts[1], lParts[2]), right)
}

data class Wire(val op: Op, val output: String)

sealed class Op
data class BinaryOp(val left: String, val action: String, val right: String): Op()
data class Not(val input: String): Op()
data class In(val input: String): Op()

