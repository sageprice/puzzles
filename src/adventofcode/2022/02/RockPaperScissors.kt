package adventofcode.`2022`.`02`

import java.io.File

fun main() {
    val input = File("src/adventofcode/2022/02/input.txt")
        .readText()
        .split("\r\n")
        .map { it.split(" ") }

    // Part 1
    println(input.map {
        when (it[1]) {
            "X" -> 1
            "Y" -> 2
            else -> 3
        }
    }.sum() + input.map {
        if (it[0] == "A" && it[1] == "X") 3
        else if (it[0] == "B" && it[1] == "Y") 3
        else if (it[0] == "C" && it[1] == "Z") 3
        else if (it[0] == "A" && it[1] == "Y") 6
        else if (it[0] == "B" && it[1] == "Z") 6
        else if (it[0] == "C" && it[1] == "X") 6
        else 0
    }.sum())

    // Part 2
    println(input.map { when (it[1]) {
        "X" -> if (it[0] == "A") 3
                else if  (it[0] == "B") 1
                else 2
        "Y" -> 3 + if (it[0] == "A") 1
                else if  (it[0] == "B") 2
                else 3
        "Z" -> 6 + if (it[0] == "A") 2
                else if  (it[0] == "B") 3
                else 1
        else -> error("bad input")
    } }.sum())
}