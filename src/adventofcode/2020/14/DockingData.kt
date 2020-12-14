package adventofcode.`2020`.`14`

import java.io.File
import java.math.BigInteger

fun main() {
  val instrs: List<Instruction> =
      File("src/adventofcode/2020/14/input.txt")
          .readLines()
          .map { extractInstruction(it) }

  // Part 1
  var mask: Mask = instrs[0] as Mask
  val mem = mutableMapOf<Int, Long>()
  instrs.forEach { instr ->
    when (instr) {
      is Mask -> mask = instr
      is Mem -> {
        mem[instr.addr] = applyMask(mask, instr.v)
      }
    }
  }
  println("Part 1: " + mem.values.sum())

  // Part 2
  val mem2 = mutableMapOf<Long, Long>()
  instrs.forEach { instr ->
    when (instr) {
      is Mask -> mask = instr
      is Mem -> {
        applyMask2(mask, instr).forEach { mem2[it] = instr.v.toLong() }
      }
    }
  }
  println("Part 2: " + mem2.values.map { BigInteger.valueOf(it) }.reduce{ a, b -> a + b})
}

/** Returns a list of addresses created by applying the mask to mem.addr. */
private fun applyMask2(mask: Mask, mem: Mem): List<Long> {
  val rAddr = Integer.toBinaryString(mem.addr).reversed()
  val rMask = mask.m.reversed()
  var addrs = listOf(rMask)
  for (i in rMask.indices) {
    if (rMask[i] == 'X') {
      addrs = addrs.map { replaceCharAt(it, i, '1', rMask) } +
          addrs.map { replaceCharAt(it, i, '0', rMask) }
    } else if (rMask[i] == '0') {
      addrs = addrs.map { replaceCharAt(it, i, if (rAddr.length > i) rAddr[i] else '0', rMask) }
    }
  }
  return addrs.map { it.reversed().toLong(2) }
}

private fun replaceCharAt(bits: String, i: Int, ch: Char, rMask: String) =
    bits.substring(0, i) + ch + (if (i + 1 < rMask.length) bits.substring(i + 1) else "")

private fun applyMask(mask: Mask, mem: String): Long {
  val rm = Integer.toBinaryString(mem.toInt()).reversed()
  val rmask = mask.m.reversed()
  val result = rmask.toCharArray()
  for (i in rmask.indices) {
    if (rmask[i] == 'X') {
      result[i] = if (rm.length > i) rm[i] else '0'
    }
  }
  return String(result).reversed().toLong(2)
}

private fun extractInstruction(line: String): Instruction {
  if (line.subSequence(0,4) == "mask") return Mask(line.split(" ").last())
  val memPattern = Regex("^mem\\[(\\d+)] = (\\d+$)")
  val memParts = memPattern.find(line)!!.groupValues
  return Mem(memParts[1].toInt(), memParts[2])
}

private sealed class Instruction
private data class Mask(val m: String): Instruction()
private data class Mem(val addr: Int, val v: String): Instruction()

