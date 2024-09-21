package adventofcode.`2015`.`12`

import java.io.File

fun main() {
  val input = File("src/adventofcode/2015/12/input.txt").readText()

  val json = parseJson(input)
  println(sumJsonEntries(json))
  println(sumJsonEntriesNoRed(json))
}

private fun sumJsonEntries(obj: JsonObject): Long {
  return when (obj) {
    is JsonString -> 0L
    is JsonInt -> obj.num
    is JsonList -> obj.entries.sumOf { sumJsonEntries(it) }
    is JsonMap -> obj.entries.values.sumOf { sumJsonEntries(it) }
  }
}

private fun sumJsonEntriesNoRed(obj: JsonObject): Long {
  return when (obj) {
    is JsonString -> 0L
    is JsonInt -> obj.num
    is JsonList -> obj.entries.sumOf { sumJsonEntriesNoRed(it) }
    is JsonMap -> {
      if (obj.entries.values.any { it is JsonString && it.content == "red"} ) {
        0L
      } else {
        obj.entries.values.sumOf { sumJsonEntriesNoRed(it) }
      }
    }
  }
}

private fun parseJson(text: String): JsonObject {
  return parseJsonHelper(text, 0).first
}

private fun parseJsonHelper(text: String, index: Int): Pair<JsonObject, Int> {
  return when (text[index]) {
    '{' -> parseJsonMap(text, index)
    '[' -> parseJsonList(text, index)
    '"' -> parseJsonString(text, index)
    else -> parseJsonInt(text, index)
  }
}

private fun parseJsonMap(text: String, index: Int): Pair<JsonMap, Int> {
  var endIdx = index+1
  val map = mutableMapOf<JsonString, JsonObject>()
  while (text[endIdx] != '}') {
    if (text[endIdx] == ',') endIdx++
    else {
      assert(text[endIdx] == '"') { "Map entry must start with key at $endIdx: ${text.substring(endIdx)}" }
      val (key, keyEnd) = parseJsonString(text, endIdx)
      assert(text[keyEnd] == ':') { "Map key is not followed by value at $endIdx: ${text.substring(keyEnd)}"}
      val (value, valueEnd) = parseJsonHelper(text, keyEnd + 1)
      map[key] = value
      endIdx = valueEnd
    }
  }
  return Pair(JsonMap(map), endIdx+1)
}

private fun parseJsonList(text: String, index: Int): Pair<JsonList, Int> {
  var endIdx = index+1
  val entries = mutableListOf<JsonObject>()
  while (text[endIdx] != ']') {
    if (text[endIdx] == ',') endIdx++
    else {
      val (obj, newIdx) = parseJsonHelper(text, endIdx)
      entries.add(obj)
      endIdx = newIdx
    }
  }
  return Pair(JsonList(entries), endIdx+1)
}

private fun parseJsonString(text: String, index: Int): Pair<JsonString, Int> {
  var output = ""
  for (i in index+1 until text.length) {
    if (text[i].isLetter()) {
      output += text[i]
    }
    else if (text[i] == '"') {
      return Pair(JsonString(output), i+1)
    }
    else throw IllegalArgumentException("Invalid char in string: ${text.substring(index, i+1)}")  //+1 to skip close double-quote
  }
  throw IllegalArgumentException("Found string with no closing quote: ${text.substring(index)}")
}

private fun parseJsonInt(text: String, index: Int): Pair<JsonInt, Int> {
  if (text[index] == '-') {
    val (posInt, endIndex) = parseJsonInt(text, index+1)
    return Pair(JsonInt(-posInt.num), endIndex)
  }
  var output = 0L
  for (i in index until text.length) {
    if (text[i].isDigit()) {
      output = (output*10 + text[i].toString().toInt())
    }
    else return Pair(JsonInt(output), i)
  }
  throw IllegalArgumentException("Found non-terminating int: ${text.substring(index)}")
}

private sealed class JsonObject {
  abstract fun toStringHelper(depth:Int): String
}

private data class JsonString(val content: String): JsonObject() {
  override fun toStringHelper(depth: Int): String {
    return " ".repeat(2*depth) + content
  }
}
private data class JsonInt(val num: Long): JsonObject() {
  override fun toStringHelper(depth: Int): String {
    return " ".repeat(2*depth) + num
  }
}
private data class JsonList(val entries: List<JsonObject>): JsonObject() {
  override fun toStringHelper(depth: Int): String {
    var out = " ".repeat(2*depth) + "[\n"
    entries.forEach { out += " ".repeat(2*depth + 2) + it.toStringHelper(depth+1).trim() + "\n" }
    out += " ".repeat(2*depth) + "]\n"
    return out
  }
}
private data class JsonMap(val entries: Map<JsonString, JsonObject>): JsonObject() {
  override fun toStringHelper(depth: Int): String {
    var out = " ".repeat(2*depth) + "{\n"
    entries.forEach {
      out += " ".repeat(2*depth+2) + it.key.toStringHelper(0) + ": " + it.value.toStringHelper(depth+2).trim() + "\n"
    }
    out += " ".repeat(2*depth) + "}\n"
    return out
  }
}
