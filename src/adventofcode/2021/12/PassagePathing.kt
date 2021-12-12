package adventofcode.`2021`.`12`

import java.io.File

fun main() {
  val cavernLinks = mutableMapOf<String, MutableList<String>>()
  for (link in File("src/adventofcode/2021/12/input.txt").readLines()) {
    val (a, b) = link.split("-")
    if (cavernLinks.containsKey(a)) cavernLinks[a]!!.add(b) else cavernLinks[a] = mutableListOf(b)
    if (cavernLinks.containsKey(b)) cavernLinks[b]!!.add(a) else cavernLinks[b] = mutableListOf(a)
  }

  // Part 1
  println(getRoutes(cavernLinks, "start", listOf("start"), double = "disallowed").size)

  // Part 2
  println(getRoutes(cavernLinks, "start", listOf("start"), double = null).size)
}

private fun getRoutes(
  links: Map<String, List<String>>,
  cavern: String,
  route: List<String>,
  double: String?
): List<List<String>> {
  val routes = mutableListOf<List<String>>()
  for (next in links[cavern]!!) {
    if (next == "end") {
      routes.add(route + next)
    } else if (next == "start") {
      continue
    } else if (next.toUpperCase() == next || !route.contains(next)) {
      routes.addAll(getRoutes(links, next, route + next, double))
    } else if (double == null) {
      routes.addAll(getRoutes(links, next, route + next, next))
    }
  }
  return routes
}