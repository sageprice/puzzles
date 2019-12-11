package adventofcode.`2019`.`10`

import java.io.File
import java.lang.Integer.max
import kotlin.math.abs

fun main() {
    // Read input file
    val asteroidField: List<List<String>> =
        File("src/adventofcode/2019/10/test_input.txt")
            .readLines().map { it.chunked(1) }

    val width = asteroidField[0].size
    val height = asteroidField.size

    val asteroids = mutableListOf<Asteroid>()
    for (i in asteroidField[0].indices) {
        for (j in asteroidField.indices) {
            if (asteroidField[j][i] == "#") {
                asteroids.add(Asteroid(i, j))
            }
        }
    }

    val occlusions = mutableListOf<MutableList<Int>>()
    repeat(asteroidField.indices.count()) {
        val row = mutableListOf<Int>()
        repeat(asteroidField[0].indices.count()) { row.add(0) }
        occlusions.add(row)
    }

//    println(asteroids)

    for (i in 0 until asteroids.size) {
        val first = asteroids[i]
        for (j in i+1 until asteroids.size) {
            val second = asteroids[j]
            if (first == second) break

            val slope = getReducedSlope(first, second)
            println("$first, $second, $slope")
            var occludedX = second.x + slope.first
            var occludedY = second.y + slope.second
            while (occludedX in 0 until width && occludedY in 0 until height) {
                occlusions[occludedY][occludedX]++
                occludedX += slope.first
                occludedY += slope.second
            }
            occludedX = first.x - slope.first
            occludedY = first.y - slope.second
            while (occludedX in 0 until width && occludedY in 0 until height) {
                occlusions[occludedY][occludedX]++
                occludedX -= slope.first
                occludedY -= slope.second
            }
            occlusions.forEach { println(it) }
        }
    }

    println()
    println()
    for (x in occlusions) { println(x) }
    println(asteroids.size)
    println(asteroids.map { Pair(it, asteroids.size - occlusions[it.y][it.x] - 1) }.maxBy { it.second })
}

/**
 * Returns the slope between first and second as a pair representing dx and dy.
 */
fun getReducedSlope(first: Asteroid, second: Asteroid): Pair<Int, Int> {
    var dx = second.x - first.x
    var dy = second.y - first.y
    if (dx == 0) return Pair(0, dy / abs(dy))
    if (dy == 0) return Pair(dx / abs(dx), 0)
    for (i in 2..max(dx, dy)) {
        while (dy % i == 0 && dx % i == 0) {
            dx /= i
            dy /= i
        }
    }
    return Pair(dx, dy)
}

data class Asteroid(val x: Int, val y: Int)