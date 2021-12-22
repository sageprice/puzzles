package adventofcode.`2019`.`18`

import java.io.File

var mazesByKeys = mutableMapOf<Set<Char>, Array<IntArray>>()
lateinit var programSpec: Array<Array<Char>>
//val lower = "abcdefghijklmnopqrstuvwxyz"
val lower = "abcdefghijklmnop"
val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
val other = ".#@"

/**
 * Solution for [Advent of Code day 18](https://adventofcode.com/2019/day/18).
 */
fun main() {
    // Read input file
    programSpec =
        File("src/adventofcode/2019/18/test_input.txt")
            .readLines().map { it.toCharArray().toTypedArray() }.toTypedArray()

    programSpec.forEach { it.forEach { print(it) }; println() }

    // Part 1
    mazesByKeys[setOf()] = generateFreshMaze()
//    val yStart = programSpec.indexOfFirst { it.contains('@') }
//    val xStart = programSpec[yStart].indexOfFirst { it == '@' }

//    recursivelyExplore(setOf(), xStart, yStart, 0)

    repeat(4) { println() }
//    val ayStart = programSpec.indexOfFirst { it.contains('a') }
//    val axStart = programSpec[ayStart].indexOfFirst { it == 'a' }
//    println(dBetween('a', 'i', setOf(), axStart, ayStart, listOf()))
    val charPaths = mutableMapOf<Pair<Char, Char>, List<Pair<Int, Set<Char>>>?>()
    for (c1 in lower) {
        val ys = programSpec.indexOfFirst { it.contains(c1) }
        val xs = programSpec[ys].indexOfFirst { it == c1 }
        val dFromStart = dBetween(c1, '@', setOf(), xs, ys, listOf())
        if (!dFromStart.isNullOrEmpty()) charPaths[Pair('@', c1)] = dFromStart
        for (c2 in lower) {
            if (c1 != c2) {
                val d = dBetween(c1, c2, setOf(), xs, ys, listOf())
                if (!d.isNullOrEmpty()) {
                    charPaths[Pair(c1, c2)] = d
                }
            }
        }
    }
    charPaths.entries.forEach { println(it) }
    println(findShortestPath("@", lower, charPaths))

//    println(mazesByKeys.keys)
//    mazesByKeys[setOf('a', 'o', 'y', 'p')]!!.forEach { it.forEach { print((if (it in 0..999999) it else "#").toString() + '\t') }; println() }
//    println(mazesByKeys[lower.toSet()]!!.flatMap { it.toList() }.filter { it > 0 }.min())
//    println(mazesByKeys[setOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l')]!!.flatMap { it.toList() }.filter { it > 0 }.min())
}

fun findShortestPath(found: String, missing: String, paths: Map<Pair<Char, Char>, List<Pair<Int, Set<Char>>>?>): Int? {
    val last = found.last()
    return missing.mapNotNull { c ->
        if (found.length == 1) {
            println("Starting $c")
        }
        val p = paths[Pair(last, c)]
        if (p == null || p.isEmpty()) null else {
            val minD = p.filter { it.second.all { found.contains(it) } }
                .map { it.first }
                .minOrNull()
            if (minD == null) null else {
                val rest = findShortestPath(found + c, missing.filterNot { it == c }, paths)
                if (rest == null) null else rest + minD
            }
        }
    }.minOrNull()
}

fun dBetween(start: Char, end: Char, requiredKeys: Set<Char>, x: Int, y: Int, visited: List<Pair<Int, Int>>): List<Pair<Int, Set<Char>>>? {
    val c = programSpec[y][x]
    // We made it!
    if (c == end) return listOf(Pair(visited.size, requiredKeys))

    // Stop if we hit a wall, have been here before, or have gone too far.
    if (c == '#' || visited.contains(Pair(x, y)) || visited.size > 500) return null

    val neededKeys = if (upper.contains(c)) {
        when {
            c.toLowerCase() == end -> return null
            c.toLowerCase() != start -> requiredKeys + c.toLowerCase()
            else -> requiredKeys
        }
    } else requiredKeys
    val left = dBetween(start, end, neededKeys, x - 1, y, visited + Pair(x, y))
    val right = dBetween(start, end, neededKeys, x + 1, y, visited + Pair(x, y))
    val up = dBetween(start, end, neededKeys, x, y - 1, visited + Pair(x, y))
    val down = dBetween(start, end, neededKeys, x, y + 1, visited + Pair(x, y))

    return listOfNotNull(left, right, up, down)
        .flatten()
        .groupBy { it.second }
        .mapValues { e -> e.value.map { it.first }.minOrNull()!! }
        .map { Pair(it.value, it.key) }
}

fun recursivelyExplore(keys: Set<Char>, x: Int, y: Int, pathLength: Int) {
    val c = programSpec[y][x]
    // Stop if we hit a wall.
    if (c == '#') return
    // Stop if we don't have a key, so cannot pass through.
    if (upper.contains(c) && !keys.contains(c.toLowerCase())) return

    val currentKeys = if (lower.contains(c)) {
        val withKey = keys + c
        if (!mazesByKeys.containsKey(withKey)) {
            mazesByKeys[withKey] = generateFreshMaze()
        }
        withKey
    } else keys
    // Stop if we've previously gotten here faster.
    if (mazesByKeys[currentKeys]!![y][x] < pathLength) return

    mazesByKeys[currentKeys]!![y][x] = pathLength
    // If we've got all the keys, we can stop
    if (currentKeys.size == 26) return
    recursivelyExplore(currentKeys, x + 1, y, pathLength + 1)
    recursivelyExplore(currentKeys, x - 1, y, pathLength + 1)
    recursivelyExplore(currentKeys, x, y + 1, pathLength + 1)
    recursivelyExplore(currentKeys, x, y - 1, pathLength + 1)
}

fun generateFreshMaze(): Array<IntArray> {
    val arr = Array(programSpec.size) { IntArray(programSpec[0].size) { -1 } }
    for (i in programSpec.indices) {
        for (j in programSpec[0].indices) {
            if (programSpec[i][j] != '#') {
                arr[i][j] = Int.MAX_VALUE
            }
        }
    }
    return arr
}

