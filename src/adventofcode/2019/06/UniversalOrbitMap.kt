package adventofcode.`2019`.`06`

import java.io.File

/**
 * Solution for [Advent of Code day 6](https://adventofcode.com/2019/day/6).
 */
fun main() {
    // Read input file
    val orbitInputs: List<String> =
            File("src/adventofcode/2019/06/input.txt").readLines()

    val orbits: List<Orbit> = orbitInputs.map {
        val orbitComponents = it.split(")")
        Orbit(orbitComponents[0], orbitComponents[1])
    }

    val planets: List<String> = orbits.flatMap { listOf(it.planet, it.star) }.distinct()

    // Part 1
    val planetMap = mutableMapOf<String, Node>()
    planets.forEach { planetMap[it] = Node(null, it, mutableListOf()) }

    orbits.forEach {
        val starNode = planetMap[it.star]!!
        val planetNode = planetMap[it.planet]!!
        starNode.planets.add(planetNode)
        planetNode.parentStar = starNode
    }

    println("Part 1, orbital checksum: ${recursiveChecksum(planetMap["COM"]!!)}")

    // Part 2
    val santa = planetMap["SAN"]!!
    val me = planetMap["YOU"]!!

    val santaOrbits = getOrbited(santa)
    val myOrbits = getOrbited(me)

    val intersectSize = myOrbits.intersect(santaOrbits).size
    println("Part 2, minimum transfers: ${santaOrbits.size + myOrbits.size - 2 * intersectSize}")
}

/** Returns a list of objects orbited by the given node. */
fun getOrbited(node: Node): List<String> {
    var pointer = node
    val orbits = mutableListOf<String>()
    while (pointer.parentStar != null) {
        pointer.parentStar?.let {
            orbits.add(it.star)
            pointer = it
        }
    }
    return orbits.asReversed()
}

/** Computes the checksum of the universal orbital map rooted at the given node. */
fun recursiveChecksum(node: Node, depth: Int = 0): Int {
//    // Iterative approach, in case of stack overflow
//    val nodeDepths: MutableList<Pair<Node, Int>> = mutableListOf(Pair(node, 0))
//    var sum = 0
//    while (nodeDepths.isNotEmpty()) {
//        val n = nodeDepths.removeAt(0)
//        sum += n.second
//        n.first.planets.forEach { nodeDepths.add(Pair(it, n.second + 1)) }
//    }
//    return sum

    // Recursive approach
    return depth + node.planets.map { recursiveChecksum(it, depth + 1) }.sum()
}

/** A relationship between two celestial bodies. The planet orbits the star. */
data class Orbit(val star: String, val planet: String)

/** Node in a graph of orbital relationships. */
data class Node(var parentStar: Node?, val star: String, val planets: MutableList<Node>)