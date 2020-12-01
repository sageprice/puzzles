package adventofcode.`2020`.`01`

import java.io.File

fun main() {
    val inputExpenses: List<Int> =
            File("src/adventofcode/2020/01/input.txt")
                    .readLines()
                    .map { it.toInt() }
    // Part 1
    println(productOfTwoSumTo2020(inputExpenses))
    // Part 2
    println(productOfThreeSumTo2020(inputExpenses))
}

private fun productOfTwoSumTo2020(xs: List<Int>): Int {
    val expenses = mutableSetOf<Int>()
    for (x in xs) {
        if (expenses.contains(2020-x)) return x * (2020-x)
        else expenses.add(x)
    }
    error("Could not find matching value")
}

private fun productOfThreeSumTo2020(xs: List<Int>): Int {
    val expenses = xs.toSet()
    for (i in xs.indices) {
        for (j in i+1 until xs.size) {
            val twoSum = xs[i]+xs[j]
            if (expenses.contains(2020-twoSum)) return xs[i] * xs[j] * (2020-twoSum)
        }
    }
    error("Could not find 3 values summing to 2020")
}

