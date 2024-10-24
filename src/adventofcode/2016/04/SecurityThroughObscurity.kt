package adventofcode.`2016`.`04`

import java.io.File
import kotlin.streams.toList

fun main() {
  val rooms = File("src/adventofcode/2016/04/input.txt").readLines().map { parseRoom(it) }
  val validRooms = rooms.filter { isValidRoom(it) }
  println(validRooms.sumOf { it.sectorId })
  validRooms.filter { decryptRoom(it).contains("northpole") }.forEach { println(it.sectorId) }
}

private fun decryptRoom(room: Room): String {
  val chars = room.code.chars().toList().filter { it.toChar() != '-' }
  return chars.map { (it - 97 + room.sectorId) % 26 + 97 }.map { it.toChar() }.joinToString("")
}

private fun isValidRoom(room: Room): Boolean {
  val charCounts =
    room.code
      .split("")
      .filter { it.isNotEmpty() && it != "-" }
      .groupBy { it }
      .mapValues { (_, v) -> v.size }
      .toList()
      .sortedWith(compareBy({ -it.second }, { it.first}))
  return charCounts.map { it.first }.take(5).joinToString("") == room.checksum
}

private fun parseRoom(line: String): Room {
  val (start, end) = line.split("[")
  val checkSum = end.substring(0, 5)
  val sectorId = start.substringAfterLast("-")
  val code = start.substring(0, start.indexOfLast { it == '-'} )
  return Room(code, sectorId.toInt(), checkSum)
}

private data class Room(
  val code: String,
  val sectorId: Int,
  val checksum: String
)