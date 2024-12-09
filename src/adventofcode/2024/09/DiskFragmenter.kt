package adventofcode.`2024`.`09`

import java.io.File

/** https://adventofcode.com/2024/day/9 */
fun main() {
  val input = File("src/adventofcode/2024/09/input.txt").readText()

  val dss = mutableListOf<DiskSpace>()
  for (i in input.indices) {
    if (i % 2 == 0) {
      dss.add(FileBlock(i / 2, input[i].toString().toLong()))
    } else {
      dss.add(FreeSpace(input[i].toString().toLong()))
    }
  }

  val compacted = compact(dss)
  println(getChecksum(compacted))
  val c2 = compact2(dss)
  println(getChecksum(c2))
}

private fun getChecksum(blocks: List<DiskSpace>): Long {
  var i = 0L
  var checksum = 0L
  for (block in blocks) for (x in 1..block.length) {
    if (block is FileBlock) checksum += i * block.name
    i++
  }
  return checksum
}

private fun compact(fragmented: List<DiskSpace>): List<DiskSpace> {
  val blocks = ArrayDeque<DiskSpace>()
  fragmented.forEach { blocks.add(it) }
  val compacted = mutableListOf<DiskSpace>()
  var free = 0L
  while (blocks.isNotEmpty()) {
    when (val next = blocks.removeFirst()) {
      is FreeSpace -> {
        var toFill = next.length
        free += toFill
        while (toFill > 0 && blocks.isNotEmpty()) {
          when (val tail = blocks.removeLast()) {
            is FreeSpace -> free += toFill
            is FileBlock -> {
              if (tail.length <= toFill) {
                compacted.add(tail)
                toFill -= tail.length
              } else {
                val used = FileBlock(tail.name, toFill)
                val excess = FileBlock(tail.name, tail.length - toFill)
                compacted.add(used)
                blocks.add(excess)
                toFill = 0
              }
            }
          }
        }
      }
      is FileBlock -> compacted.add(next)
    }
  }
  return compacted
}

private fun compact2(fragmented: List<DiskSpace>): List<DiskSpace> {
  var blocks = fragmented.toList()
  var i = blocks.size - 1
  while (i > 0) {
    val curr = blocks[i]
    if (curr is FreeSpace) {
      i--
      continue
    }
    val newIndex = findInsertionIndex(curr as FileBlock, blocks.subList(0, i))
    if (newIndex == null) {
      i--
      continue
    }
    val head = blocks.subList(0, newIndex)
    // Replace old position w/ free of same length
    val tail = (blocks.subList(newIndex + 1, i) + FreeSpace(curr.length)) + blocks.subList(i+1, blocks.size)
    blocks = if (blocks[newIndex].length == curr.length) {
      (head + curr)
    } else {
      // Make sure to append the remaining free space.
      (head + curr + FreeSpace(blocks[newIndex].length - curr.length))
    } + tail
  }
  return blocks
}

private fun findInsertionIndex(newBlock: FileBlock, blocks: List<DiskSpace>): Int? {
  return blocks.indices.firstOrNull { i ->
    blocks[i] is FreeSpace && blocks[i].length >= newBlock.length
  }
}

private sealed class DiskSpace(open val length: Long)
private data class FileBlock(
  val name: Int,
  override val length: Long
): DiskSpace(length)
private data class FreeSpace(
  override val length: Long
): DiskSpace(length)
