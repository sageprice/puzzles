package adventofcode.`2019`.`12`

import kotlin.math.abs

fun main() {
    val m1 = Moon(Position(4, 1, 1), Velocity(0, 0, 0))
    val m2 = Moon(Position(11, -18, -1), Velocity(0, 0, 0))
    val m3 = Moon(Position(-2, -10, -4), Velocity(0, 0, 0))
    val m4 = Moon(Position(-7, -2, 14), Velocity(0, 0, 0))

    // Example 1
//    val m1 = Moon(Position(-1, 0, 2), Velocity(0, 0, 0))
//    val m2 = Moon(Position(2, -10, -7), Velocity(0, 0, 0))
//    val m3 = Moon(Position(4, -8, 8), Velocity(0, 0, 0))
//    val m4 = Moon(Position(3, 5, -1), Velocity(0, 0, 0))

    // Example 2
//    val m1 = Moon(Position(-8, -10, 0), Velocity(0, 0, 0))
//    val m2 = Moon(Position(5, 5, 10), Velocity(0, 0, 0))
//    val m3 = Moon(Position(2, -7, 3), Velocity(0, 0, 0))
//    val m4 = Moon(Position(9, -8, -3), Velocity(0, 0, 0))

    val moons: List<Moon> = listOf(m1, m2, m3, m4)

    // Part 1
    val nSteps = 1000
    for (i in 1..nSteps) {
        val deltaVs = moons.map { systemDeltaV(it.p, moons.map { m -> m.p }) }
        for (j in moons.indices) {
            moons[j].v += deltaVs[j]
            moons[j].p = moons[j].p.move(moons[j].v)
        }
    }
    println("Part 1: total energy after $nSteps = ${moons.map { it.energy() }.sum()}")

    // Part 2 -- idea: axes are independent, calculate repetition cycle for x, y, z and get LCM.
    // reset moons:
    val moons2 =
    // Actual
            listOf(
                    Moon(Position(4, 1, 1), Velocity(0, 0, 0)),
                    Moon(Position(11, -18, -1), Velocity(0, 0, 0)),
                    Moon(Position(-2, -10, -4), Velocity(0, 0, 0)),
                    Moon(Position(-7, -2, 14), Velocity(0, 0, 0)))
    // Example 1
//                    Moon(Position(-1, 0, 2), Velocity(0, 0, 0)),
//                    Moon(Position(2, -10, -7), Velocity(0, 0, 0)),
//                    Moon(Position(4, -8, 8), Velocity(0, 0, 0)),
//                    Moon(Position(3, 5, -1), Velocity(0, 0, 0)))
    // Example 2
//                    Moon(Position(-8, -10, 0), Velocity(0, 0, 0)),
//                    Moon(Position(5, 5, 10), Velocity(0, 0, 0)),
//                    Moon(Position(2, -7, 3), Velocity(0, 0, 0)),
//                    Moon(Position(9, -8, -3), Velocity(0, 0, 0)))
    var xs: List<Int> = moons2.map { it.p.x } + moons2.map { it.v.dx }
    var ys: List<Int> = moons2.map { it.p.y } + moons2.map { it.v.dy }
    var zs: List<Int> = moons2.map { it.p.z } + moons2.map { it.v.dz }
    val xConfig = mutableMapOf(xs to 0)
    val yConfig = mutableMapOf(ys to 0)
    val zConfig = mutableMapOf(zs to 0)
    var i = 0
    var xRep = -1
    var yRep = -1
    var zRep = -1
    while (xRep == -1 || yRep == -1 || zRep == -1) {
        i++
        val deltaVs = moons2.map { systemDeltaV(it.p, moons2.map { m -> m.p }) }
        for (j in moons2.indices) {
            moons2[j].v += deltaVs[j]
            moons2[j].p = moons2[j].p.move(moons2[j].v)
        }
        if (xRep == -1) {
            xs = moons2.map { it.p.x } + moons2.map { it.v.dx }
            if (xConfig.containsKey(xs)) {
                xRep = i
            } else {
                xConfig[xs] = i
            }
        }
        if (yRep == -1) {
            ys = moons2.map { it.p.y } + moons2.map { it.v.dy }
            if (yConfig.contains(ys)) {
                yRep = i
            } else {
                yConfig[ys] = i
            }
        }
        if (zRep == -1) {
            zs = moons2.map { it.p.z } + moons2.map { it.v.dz }
            if (zConfig.contains(zs)) {
                zRep = i
            } else {
                zConfig[zs] = i
            }
        }
    }
    // GCD and LCM taken from Rosetta Code: https://rosettacode.org/wiki/Least_common_multiple#Kotlin
    fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
    fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b
    println("Part 2 answer: ${lcm(lcm(xRep.toLong(), yRep.toLong()), zRep.toLong())}")
}

data class Moon(var p: Position, var v: Velocity)

fun Moon.energy(): Int {
    return p.energy() * v.energy()
}

data class Position(val x: Int, val y: Int, val z: Int)

fun Position.energy(): Int {
    return abs(x) + abs(y) + abs(z)
}

fun Position.move(v: Velocity): Position {
    return Position(this.x + v.dx, this.y + v.dy, this.z + v.dz)
}

data class Velocity(val dx: Int, val dy: Int, val dz: Int)

fun Velocity.energy(): Int {
    return abs(dx) + abs(dy) + abs(dz)
}

operator fun Velocity.plus(other: Velocity): Velocity {
    return Velocity(this.dx + other.dx, this.dy + other.dy, this.dz + other.dz)
}

fun systemDeltaV(moon: Position, system: List<Position>): Velocity {
    return system.filterNot { it == moon }
            .map { moon.deltaV(it) }
            .reduce { a, b -> a + b }
}

fun Position.deltaV(other: Position): Velocity {
    return Velocity(
            normalizedComparison(x, other.x),
            normalizedComparison(y, other.y),
            normalizedComparison(z, other.z)
    )
}

fun normalizedComparison(a: Int, b: Int): Int {
    return when {
        a > b -> -1
        a == b -> 0
        else -> 1
    }
}