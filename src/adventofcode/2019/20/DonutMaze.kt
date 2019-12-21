package adventofcode.`2019`.`20`

import java.io.File

/**
 * Solution for [Advent of Code day 20](https://adventofcode.com/2019/day/20).
 */
fun main() {
    // Read input file
    val donut =
        File("src/adventofcode/2019/20/input.txt").readLines()

    // Part 1
    val endPoints = getEndPoints(donut)
//    println(endPoints)

    val warpPoints = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()
    var start: Pair<Int, Int> = Pair(0, 0)
    var end: Pair<Int, Int> = Pair(0, 0)

    endPoints.groupBy { it.label }.forEach { (label, points) ->
        when (label) {
            "AA" -> start = Pair(points[0].x, points[0].y)
            "ZZ" -> end = Pair(points[0].x, points[0].y)
            else -> {
                warpPoints[Pair(points[0].x, points[0].y)] = Pair(points[1].x, points[1].y)
                warpPoints[Pair(points[1].x, points[1].y)] = Pair(points[0].x, points[0].y)
            }
        }
    }
//    endPoints.groupBy { it.label }.filter { it.value.size >= 2 }.forEach { v -> println("$v") }
    val minPaths = Array(donut.size) { Array(donut[2].length + 1) { 0 } }
    for (y in minPaths.indices) {
        for (x in minPaths[y].indices) {
            if (x in donut[y].indices) {
                minPaths[y][x] = when (donut[y][x]) {
                    '#' -> 0
                    '.' -> Int.MAX_VALUE
                    else -> 0
                }
            }
        }
    }
    val minPathFilled = exploreMaze(minPaths, start, warpPoints)
//    minPathFilled.forEachIndexed { i, arr ->
//        print("$i:\t")
//        arr.forEach {
//            val v = if (it == Int.MAX_VALUE) -1 else it
//            print("$v\t")
//        }
//        println()
//    }
    println("Part 1: ${minPathFilled[end.second][end.first]}")
}

fun exploreMaze(minPaths: Array<Array<Int>>, start: Pair<Int, Int>, warps: Map<Pair<Int, Int>, Pair<Int, Int>>): Array<Array<Int>> {
    val resumePoints = mutableListOf<ExplorePoint>(ExplorePoint(start.first, start.second, 0))
    loop@ while (resumePoints.isNotEmpty()) {
        val point = resumePoints.removeAt(0)
        when {
            minPaths[point.y][point.x] <= point.d -> continue@loop
            minPaths[point.y][point.x] > point.d -> {
                minPaths[point.y][point.x] = point.d
                resumePoints.add(ExplorePoint(point.x + 1, point.y, point.d + 1))
                resumePoints.add(ExplorePoint(point.x - 1, point.y, point.d + 1))
                resumePoints.add(ExplorePoint(point.x, point.y + 1, point.d + 1))
                resumePoints.add(ExplorePoint(point.x, point.y - 1, point.d + 1))
                if (warps.containsKey(Pair(point.x, point.y))) {
                    val target = warps[Pair(point.x, point.y)] ?: error("That's not a warp point! $point")
                    resumePoints.add(ExplorePoint(target.first, target.second, point.d + 1))
                }
            }
        }
    }
    return minPaths
}

data class ExplorePoint(val x: Int, val y: Int, val d: Int)

fun getEndPoints(maze: List<String>): List<EndPoint> {
    val endPoints = mutableListOf<EndPoint>()
    // Left, Right, Inner Left, Inner Right
    maze.forEachIndexed { i, s ->
        if (s[0].isLetter()) {
            endPoints.add(EndPoint(2, i, s.substring(0, 2)))
        }
        if (s.last().isLetter() && s.length > 85) {
            endPoints.add(EndPoint(s.lastIndex - 2, i, s.substring(s.lastIndex - 1)))
        }
        if (s[29].isLetter()) {
            endPoints.add(EndPoint(28, i, s.substring(29, 31)))
        }
        if (s.length > 84 && s[84].isLetter()) {
            endPoints.add(EndPoint(86, i, s.substring(84, 86)))
        }
    }
    // Top
    maze.first().forEachIndexed { i, c ->
        if (c.isLetter()) {
            endPoints.add(EndPoint(i, 2, c.toString() + maze[1][i]))
        }
    }
    // Bottom
    maze.last().forEachIndexed { i, c ->
        if (c.isLetter()) {
            endPoints.add(EndPoint(i, maze.lastIndex - 2, maze[maze.lastIndex-1][i].toString() + c))
        }
    }
    // Inner Top
    maze[29].forEachIndexed { i, c ->
        if (c.isLetter()) {
            endPoints.add(EndPoint(i, 28, c.toString() + maze[30][i]))
        }
    }
    // Inner Bottom
    maze[87].forEachIndexed { i, c ->
        if (c.isLetter()) {
            endPoints.add(EndPoint(i, 88, maze[86][i].toString() + c))
        }
    }
    return endPoints
}

data class EndPoint(val x: Int, val y: Int, val label: String)