package adventofcode.`2016`.`07`

import java.io.File

fun main() {
  val lines = File("src/adventofcode/2016/07/input.txt").readLines()
  println(lines.count { supportsTls(it) })
  println(lines.count { supportsSsl(it) })
}

private fun supportsTls(str: String): Boolean {
  val chunks = str.split("[", "]")
  for (i in 1 until chunks.size step 2) if (hasAbba(chunks[i])) return false
  for (i in chunks.indices step 2) if (hasAbba(chunks[i])) return true
  return false
}

private fun hasAbba(str: String): Boolean {
  for (i in 3 until str.length) {
    if (str[i-3] == str[i] && str[i-1] == str[i-2] && str[i] != str[i-1]) return true
  }
  return false
}

private fun supportsSsl(str: String): Boolean {
  val chunks = str.split("[", "]")
  val abas = mutableSetOf<String>()
  val babs = mutableSetOf<String>()
  for (i in chunks.indices) {
    if (i % 2 == 0) {
      abas.addAll(getABAs(chunks[i]))
    } else {
      babs.addAll(getABAs(chunks[i]))
    }
  }
  return abas.any { aba -> "${aba[1]}${aba.first()}${aba[1]}" in babs }
}

private fun getABAs(str: String): Set<String> {
  val abas = mutableSetOf<String>()
  for (i in 2 until str.length) {
    if (str[i-2] == str[i] && str[i-1] != str[i]) {
      abas.add(str.substring(i-2, i+1))
    }
  }
  return abas
}
