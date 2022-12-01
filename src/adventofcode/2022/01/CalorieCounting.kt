package adventofcode.`2022`.`01`

import java.io.File

fun main() {
    val elves: List<List<Int>> =
        File("src/adventofcode/2022/01/input.txt")
            .readText()
            .split("\r\n\r\n") // fuckin windows
            .filter { it.isNotBlank() }
            .map {it.split("\r\n").filter { x -> x.isNotEmpty() }.map { x -> x.toInt() } }

    println("Part 1: ${elves.maxOfOrNull { it.sum() }}")
    println("Part 2: ${elves.map { it.sum() }.sortedDescending().subList(0, 3).sum()}")
}