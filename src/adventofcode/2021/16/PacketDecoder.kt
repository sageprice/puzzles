package adventofcode.`2021`.`16`

import java.io.File

fun main() {
  val text = File("src/adventofcode/2021/16/input.txt").readText()
  val binStr: String = text.map { hexCharToBinary(it) }.joinToString("")

  // Part 1
  val packet = parsePacket(binStr)
  println(getVersionSum(packet))

  // Part 2
  println(execute(packet))
}

private fun getVersionSum(packet: Packet): Int {
  return when (packet) {
    is Literal -> packet.version
    is Operator -> packet.version + packet.packets.sumBy { getVersionSum(it) }
  }
}

private fun execute(packet: Packet): Long {
  return when (packet) {
    is Literal -> packet.getValue()
    is Operator -> {
      val results = packet.packets.map { execute(it) }
      when (packet.typeId) {
        0 -> results.sum()
        1 -> results.reduce { a, b -> a*b }
        2 -> results.minOrNull()!!
        3 -> results.maxOrNull()!!
        5 -> {
          assert(results.size == 2)
          val (a: Long, b: Long) = results
          if (a > b) 1L else 0L
        }
        6 -> {
          assert(results.size == 2)
          val (a, b) = results
          if (a < b) 1L else 0L
        }
        7 -> {
          assert(results.size == 2)
          val (a, b) = results
          if (a == b) 1L else 0L
        }
        else -> error("Bad packet $packet")
      }
    }
  }
}

// Return the packet and length of the packet.
private fun parsePacket(bits: String): Packet {
  val version = toDecimalInt(bits.subSequence(0, 3))
  val typeId = toDecimalInt(bits.subSequence(3, 6))
  return if (typeId == 4) {
    Literal(version, typeId, bits.substring(6))
  } else {
    return Operator(
      version = version,
      typeId = typeId,
      lengthTypeId = bits[6].toString().toInt(),
      data = bits.substring(7)
    )
  }
}

private sealed class Packet(
  open val version: Int,
  open val typeId: Int,
) {
  abstract fun length(): Int
}

private data class Literal(
  override val version: Int,
  override val typeId: Int,
  var data: String
): Packet(version, typeId) {
  init {
    getValue()
  }
  override fun length(): Int = data.length + 6

  fun getValue(): Long {
    val chunks = this.data.chunked(5)
    var x = 0L
    for (i in chunks.indices) {
      val chunk = chunks[i]
      x *= 16 // 2^4 -- packet chunks are 4 bits
      x += toDecimalInt(chunk.substring(1))
      if (chunk[0] == '0') {
        val contentLength = (i+1)*5
        data = data.substring(0, contentLength)
        return x
      }
    }
    error("Should have finished, cannot parse literal data: $this")
  }
}

private data class Operator(
  override val version: Int,
  override val typeId: Int,
  val lengthTypeId: Int,
  var data: String,
) : Packet(version, typeId) {
  var packets: List<Packet> = parseSubPackets()

  override fun length(): Int =
    7 + packets.sumBy { it.length() } + if (lengthTypeId == 0) 15 else 11

  private fun parseSubPackets(): List<Packet> {
    val packets = mutableListOf<Packet>()
    if (lengthTypeId == 0) {
      val totalPacketBits = toDecimalInt(this.data.substring(0, 15))
      var parsedBits = 0
      while (parsedBits < totalPacketBits) {
        val nextPacket = parsePacket(this.data.substring(15 + parsedBits))
        parsedBits += nextPacket.length()
        packets.add(nextPacket)
      }
      data = data.substring(0, 15 + parsedBits)
      if (parsedBits > totalPacketBits) error("Packets are too long, $parsedBits > $totalPacketBits: $packets")
    } else {
      val numPackets = toDecimalInt(this.data.substring(0, 11))
      for (i in 1..numPackets) {
        val nextPacket = parsePacket(this.data.substring(11 + packets.sumBy { it.length() }))
        packets.add(nextPacket)
      }
      data = data.substring(0, 11 + packets.sumBy { it.length() })
    }
    return packets
  }
}

private fun toDecimalInt(str: CharSequence): Int {
  var x = 0
  for (c in str) {
    x *= 2
    when (c) {
      '0' -> {}
      '1' -> x++
      else -> error("Invalid binary string: [$str]")
    }
  }
  return x
}

private fun hexCharToBinary(c: Char): String {
  return when (c) {
    '0' -> "0000"
    '1' -> "0001"
    '2' -> "0010"
    '3' -> "0011"
    '4' -> "0100"
    '5' -> "0101"
    '6' -> "0110"
    '7' -> "0111"
    '8' -> "1000"
    '9' -> "1001"
    'A' -> "1010"
    'B' -> "1011"
    'C' -> "1100"
    'D' -> "1101"
    'E' -> "1110"
    'F' -> "1111"
    else -> error("Invalid input $c")
  }
}
