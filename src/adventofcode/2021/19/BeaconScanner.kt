package adventofcode.`2021`.`19`

import java.io.File
import kotlin.math.abs

fun main() {
  val input = File("src/adventofcode/2021/19/input.txt")
    .readText()
    .split("\n\n")

  val scannerViews = input.map {
    val lines = it.split("\n")
    val positions = lines.subList(1, lines.size)
    ScannerView(positions.map { line ->
      val (x, y, z) = line.split(",")
      Coord(x.toInt(), y.toInt(), z.toInt())
    })
  }

  // Part 1
  val unlinkedScanners = scannerViews.subList(1, scannerViews.size).toMutableList()
  // Used for part 2. Initialize with the assumption that scanner 0 is at the origin and correctly oriented.
  val scannerLocations = mutableListOf(Coord(0, 0, 0))

  // Assume that scanner 0 is correctly oriented, so all of its beacons are as well.
  val fixedBeacons = scannerViews.first().beacons.toMutableSet()
  loop@ while (unlinkedScanners.isNotEmpty()) {
    for (i in unlinkedScanners.indices) {
      for (orientation in getRotations(unlinkedScanners[i])) {
        // Consider every pair of beacons for potential collisions. If we fix two points as overlapping, we then need to
        // adjust all other points in one set by the vector between the two points to get the overlaps.
        for (goodBeacon in fixedBeacons) {
          for (otherBeacon in orientation.beacons) {
            val xAdj = goodBeacon.x - otherBeacon.x
            val yAdj = goodBeacon.y - otherBeacon.y
            val zAdj = goodBeacon.z - otherBeacon.z
            var matches = 0
            for (beacon in orientation.beacons) {
              val adjBeacon = Coord(beacon.x + xAdj, beacon.y + yAdj, beacon.z + zAdj)
              if (fixedBeacons.contains(adjBeacon)) {
                matches++
              }
            }
            if (matches >= 12) {
              // We know the relative scanner position, save it.
              scannerLocations.add(Coord(xAdj, yAdj, zAdj))
              // Add all the beacons to our tracking set.
              fixedBeacons.addAll(
                orientation.beacons.map { beacon ->
                  Coord(beacon.x + xAdj, beacon.y + yAdj, beacon.z + zAdj)
                })
              // We've found this scanner, it's now linked in so remove from the list.
              unlinkedScanners.removeAt(i)
              continue@loop
            }
          }
        }
      }
    }
    error("Could not find a matching scanner in remaining ${unlinkedScanners.size} scanners")
  }
  println(fixedBeacons.size)

  // Part 2
  var maxDistance = 0
  for (a in scannerLocations) for (b in scannerLocations) {
    if (a == b) continue
    val d = abs(a.x - b.x) + abs(a.y - b.y) + abs(a.z - b.z)
    if (d > maxDistance) maxDistance = d
  }
  println(maxDistance)
}

private data class Coord(val x: Int, val y: Int, val z: Int)

private data class ScannerView(val beacons: List<Coord>)

private fun getRotations(scanner: ScannerView): List<ScannerView> {
  // Note: half of these are impossible. But we only care about the matches, so some misses aren't a problem.
  // Realistically this doubles our run time, but we're still fast enough so whatever.
  val rotations = mutableListOf(scanner)
  rotations.add(ScannerView(scanner.beacons.map { Coord(it.x, it.z, it.y) }))
  rotations.add(ScannerView(scanner.beacons.map { Coord(it.y, it.x, it.z) }))
  rotations.add(ScannerView(scanner.beacons.map { Coord(it.y, it.z, it.x) }))
  rotations.add(ScannerView(scanner.beacons.map { Coord(it.z, it.x, it.y) }))
  rotations.add(ScannerView(scanner.beacons.map { Coord(it.z, it.y, it.x) }))
  val allRotations = mutableListOf<ScannerView>()
  for (a in listOf(-1, 1)) {
    for (b in listOf(-1, 1)) {
      for (c in listOf(-1, 1)) {
        for (s in rotations) {
          allRotations.add(
            ScannerView(s.beacons.map { Coord(a*it.x, b*it.y, c*it.z) }))
        }
      }
    }
  }
  return allRotations
}
