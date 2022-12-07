package adventofcode.`2022`.`07`

import java.io.File

fun main() {
  val text =
    File("src/adventofcode/2022/07/input.txt")
      .readText()
      .split("\r\n$ ")

  val files = mutableMapOf<String, Long>()
  var pwd = "root" // Simpler than dealing with the actual filename of "/"

  // Skip the first command, pretend we're already in the root dir.
  (text.slice(1 until text.size)).forEach {chunk ->
    val lines = chunk.split("\r\n")
    val command = lines.first()
    if (command.startsWith("cd")) {
      check(lines.size == 1) { "Too many lines for cd command: $lines"}
      if (command.split(" ").last() == "..") {
        if (pwd != "root") {
          pwd = pwd.replaceAfterLast("/", "").removeSuffix("/")
        }
      } else {
        pwd += "/${lines.last()}"
      }
    } else if (command == "ls") {
      for (line in lines.slice(1 until lines.size)) {
        val (a, b) = line.split(" ")
        // Only track files. If there is something in a dir, we'll cd into it later.
        if (a != "dir") files["$pwd/$b"] = a.toLong()
      }
    } else error("Unrecognized command: [$chunk]")
  }

  val dirs: List<String> = files.keys.flatMap { k ->
    val dirNames = k.split("/")
    (1 until dirNames.size).map { dirNames.subList(0, it).joinToString("/") }
  }.distinct()

  val dirToSize = dirs.associateWith { d -> files.filter { e -> e.key.startsWith(d) }.values.sum() }

  // Part 1
  println(dirToSize.values.sumOf { if (it < 100_000) it else 0 })

  // Part 2
  val amtToDelete = files.values.sum() - (70_000_000 - 30_000_000)
  println(dirToSize.values.filter { it > amtToDelete }.min())
}
