package adventofcode.`2022`.`13`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2022/13/input.txt")
    .readText()
    .split("\r\n\r\n")

  val parsedInput = input.map { ls ->
    val (a, b) = ls.split("\r\n")
    Pair(parsePacket(a), parsePacket(b))
  }

  // Part 1
  println(parsedInput.mapIndexed { idx, (a, b) -> if (a precedes b) idx+1 else 0 }.sum())

  // Part 2
  val firstDividerPacket = parsePacket("[[2]]")
  val secondDividerPacket = parsePacket("[[6]]")
  val sortedPackets =
    (listOf(firstDividerPacket, secondDividerPacket) + parsedInput.flatMap { (a, b) -> listOf(a, b) })
      .sortedWith { a, b -> a compareTo b }
  println((sortedPackets.indexOf(firstDividerPacket) + 1) * (sortedPackets.indexOf(secondDividerPacket) + 1))
}

private fun parsePacket(str: String): Packet = recursivelyParsePacket(str).first

private fun recursivelyParsePacket(str: String): Pair<Packet, Int> {
  var idx = 0
  return if (str.first() == '[') {
    idx++
    val packets = mutableListOf<Packet>()
    while (idx != str.length && str[idx] != ']') {
      val (newPacket, endIndex) = recursivelyParsePacket(str.substring(idx))
      packets.add(newPacket)
      idx += endIndex
      if (str[idx] == ',') idx++
    }
    Pair(PacketList(packets), idx + 1)
  } else {
    val separatorIndex = str.indexOfFirst { c -> c == ',' || c == ']' }
    Pair(Constant(str.substring(0, separatorIndex).toInt()), separatorIndex)
  }
}

private sealed class Packet
private data class Constant(val x: Int): Packet()
private data class PacketList(val packets: List<Packet>): Packet()

private infix fun Packet.precedes(other: Packet): Boolean = this compareTo other < 0

/**
 * Java-like compareTo function. Returns:
 *  < 0 when this < other
 *  ==0 when this == other
 *  > 0 when this > other
 */
private infix fun Packet.compareTo(other: Packet): Int {
  when {
    this is Constant && other is Constant -> return x - other.x
    this is PacketList && other is PacketList -> {
      this.packets.zip(other.packets).forEach { (a, b) ->
        val comparison = a compareTo b
        if (comparison != 0) return comparison
      }
      return this.packets.size - other.packets.size
    }
    this is PacketList && other is Constant -> return this compareTo PacketList(listOf(other))
    else -> return PacketList(listOf(this)) compareTo other
  }
}