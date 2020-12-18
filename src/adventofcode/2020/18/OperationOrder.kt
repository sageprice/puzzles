package adventofcode.`2020`.`18`

import java.io.File

fun main() {
  val input: List<List<String>> =
      File("src/adventofcode/2020/18/input.txt")
          .readLines()
          .map {
            it // Simplify tokenization by spacing parens
                .replace("(", "( ")
                .replace(")", " )")
                .split(" ") }
  // Part 1
  val math = input.map { parse(it).first }
  println("Part 1: " + math.map { evaluate(it) }.sum())

  // Part 2
  println("Part 2: " + math.map { evaluate(transformPrecedence(it)) }.sum())
}

private fun transformPrecedence(node: Node): Node {
  return when (node) {
    is Paren -> Paren(transformPrecedence(node.sub))
    is Num -> node
    is Add -> {
      val newL = transformPrecedence(node.l)
      val newR = transformPrecedence(node.r)
      if (newL is Multiply) {
        Multiply(newL.l, Add(newL.r, newR))
      } else Add(transformPrecedence(newL), transformPrecedence(newR))
    }
    is Multiply -> Multiply(transformPrecedence(node.l), transformPrecedence(node.r))
  }
}

private fun evaluate(node: Node): Long {
  return when (node) {
    is Paren -> evaluate(node.sub)
    is Multiply -> evaluate(node.l) * evaluate(node.r)
    is Add -> evaluate(node.l) + evaluate(node.r)
    is Num -> node.v
  }
}

private fun parse(tokens: List<String>): Pair<Node, Int> {
  var i = 0
  var left: Node? = null
  while (i < tokens.size) {
    if (left == null) {
      val leftExpr = parseExpr(tokens)
      left = leftExpr.first
      i += leftExpr.second
    } else {
      val rightExpr = parseExpr(tokens.subList(i+1, tokens.size))
      val right = rightExpr.first
      left = (if (tokens[i] == "*") Multiply(left, right) else Add(left, right))
      i += 1 + rightExpr.second
    }
  }
  return Pair(left ?: error("Unable to parse expression from $tokens"), i)
}

private fun parseExpr(tokens: List<String>): Pair<Node, Int> {
  if (tokens[0] == "(") {
    val closeParenIndex = findMatch(tokens)
    val parenExpr = parse(tokens.subList(1, closeParenIndex))
    return Pair(Paren(parenExpr.first), closeParenIndex + 1)
  }
  return Pair(Num(tokens[0].toLong()), 1)
}

private fun findMatch(tokens: List<String>): Int {
  var depth = 0
  for (i in tokens.indices) {
    if (tokens[i] == "(") {
      depth++
    } else if (tokens[i] == ")") {
      depth--
      if (depth == 0) return i
    }
  }
  error("Could not find close paren in $tokens")
}

private sealed class Node
private data class Add(val l: Node, val r: Node): Node()
private data class Multiply(val l: Node, val r: Node): Node()
private data class Num(val v: Long): Node()
private data class Paren(val sub: Node): Node()