package adventofcode.`2019`.`10`

import java.io.File
import java.lang.Integer.max
import kotlin.math.abs

/**
 * Solution for [Advent of Code day 10](https://adventofcode.com/2019/day/10).
 *
 * Note: actually broken for the large example, but it worked with the asteroid
 * field I actually had to solve. Lucky me.
 */
fun main() {
    // Read input file
    val asteroidField: List<List<String>> =
        File("src/adventofcode/2019/10/input.txt")
            .readLines().map { it.chunked(1) }

    val asteroids = mutableListOf<Asteroid>()
    for (i in asteroidField[0].indices) {
        for (j in asteroidField.indices) {
            if (asteroidField[j][i] == "#") {
                asteroids.add(Asteroid(i, j))
            }
        }
    }

    // Part 1
    val occlusions = getOcclusions(asteroidField, asteroids)

    val bestStation =
            asteroids
                    .map { Pair(it, asteroids.size - occlusions[it.y][it.x] - 1) }
                    .maxByOrNull { it.second }
    println("Part 1: $bestStation")

    // Part 2
    // Explanation of thought process:
    //  1. Start by splitting the asteroids into quadrants so we can sort them easily
    //  2. Order each quadrant by slope, then by magnitude of distance when slope is the same
    //  3. Re-combine quadrants, ordered clockwise, into one list
    //  4. Iterate over list, destroying an asteroid each time it has a different slope from the last asteroid that
    //     we destroyed
    //  5. Remove any destroyed asteroids from the ordered list. Re-do (4) until all asteroids are destroyed
    //  6. Grab the 6th asteroid.
    val laserStation: Asteroid = bestStation!!.first

    val angleRelations =
            asteroids
                    .filterNot { it == laserStation }
                    .map { CosmicConnection(it, getReducedSlope(laserStation, it), dSquared(laserStation, it)) }
    val q1 =
            angleRelations
                    .filter { (it.slope.first >= 0) && (it.slope.second < 0) } // up right
                    .sortedWith(
                        compareBy(
                                {
                                    if (it.slope.first == 0) {
                                        -100.0
                                    } else {
                                        it.slope.second.toDouble() / it.slope.first.toDouble()
                                    }
                                },
                                { it.d2.toDouble() }
                        )
                    )
    val q2 =
            angleRelations
                    .filter { it.slope.first > 0 && it.slope.second >= 0 } // down right
                    .sortedWith(
                            compareBy(
                                    {
                                        it.slope.second.toDouble() / it.slope.first.toDouble()
                                    },
                                    { it.d2.toDouble() }
                            )
                    )
    val q3 =
            angleRelations
                    .filter { it.slope.first <= 0 && it.slope.second > 0 } // down left
                    .sortedWith(
                            compareBy(
                                    {
                                        if (it.slope.first == 0) {
                                            -50.0
                                        } else {
                                            it.slope.second.toDouble() / it.slope.first.toDouble()
                                        }
                                    },
                                    { it.d2.toDouble() }
                            )
                    )
    val q4 =
            angleRelations
                    .filter { it.slope.first < 0 && it.slope.second <= 0 } // up left
                    .sortedWith(
                            compareBy(
                                    { it.slope.second.toDouble() / it.slope.first.toDouble() },
                                    { it.d2.toDouble() }
                            )
                    )

    val connections = mutableListOf<CosmicConnection>()
    connections.addAll(q1)
    connections.addAll(q2)
    connections.addAll(q3)
    connections.addAll(q4)

    var lastSlope = Pair(0, 0)
    val laseredAsteroids = mutableListOf<CosmicConnection>()
    while (connections.isNotEmpty()) {
        for (cc in connections) {
            if (lastSlope != cc.slope) {
                laseredAsteroids.add(cc)
                lastSlope = cc.slope
            }
        }
        lastSlope = Pair(0, 0)
        connections.removeAll(laseredAsteroids)
    }
    val theone = laseredAsteroids[199]
    println("Part 2: ${theone.asteroid.x * 100 + theone.asteroid.y}")
}

fun getOcclusions(asteroidField: List<List<String>>, asteroids: List<Asteroid>): List<List<Int>> {
    val occlusions = mutableListOf<MutableList<Int>>()
    repeat(asteroidField.indices.count()) {
        val row = mutableListOf<Int>()
        repeat(asteroidField[0].indices.count()) { row.add(0) }
        occlusions.add(row)
    }

//    println(asteroids)
    val width = asteroidField[0].size
    val height = asteroidField.size

    for (i in asteroids.indices) {
        val first = asteroids[i]
        for (j in i+1 until asteroids.size) {
            val second = asteroids[j]

            val slope = getReducedSlope(first, second)
//            println("$first, $second, $slope")
            if (isOccludedPair(first, second, slope, asteroidField)) {
                continue
            }
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
//            occlusions.forEachIndexed { ind, l -> println("$ind: $l") }
        }
    }
    return occlusions
}

fun isOccludedPair(first: Asteroid, second: Asteroid, slope: Pair<Int, Int>, field: List<List<String>>): Boolean {
    var x = first.x + slope.first
    var y = first.y + slope.second
    while (x != second.x || y != second.y) {
        if (field[y][x] == "#") {
//            println("Asteroids $first and $second are occluded by [$x, $y]")
            return true
        }
        x += slope.first
        y += slope.second
    }
    return false
}

/**
 * Returns the slope between first and second as a pair representing dx and dy.
 */
fun getReducedSlope(first: Asteroid, second: Asteroid): Pair<Int, Int> {
    var dx = second.x - first.x
    var dy = second.y - first.y
    if (dx == 0) return Pair(0, dy / abs(dy))
    if (dy == 0) return Pair(dx / abs(dx), 0)
    for (i in 2..max(abs(dx), abs(dy))) {
        while (dy % i == 0 && dx % i == 0) {
            dx /= i
            dy /= i
        }
    }
    return Pair(dx, dy)
}

fun dSquared(first: Asteroid, second: Asteroid): Int {
    val dx = first.x - second.x
    val dy = first.y - second.y
    return dx * dx + dy * dy
}

data class Asteroid(val x: Int, val y: Int)

data class CosmicConnection(val asteroid: Asteroid, val slope: Pair<Int, Int>, val d2: Int)