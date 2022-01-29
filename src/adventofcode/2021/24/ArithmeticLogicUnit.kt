package adventofcode.`2021`.`24`

import java.io.File

/**
 * A day more about decrypting the input than writing the code. See input.txt for annotations, I
 * removed some lines which are useless and highlighted the key lines. Some basic things to note:
 *  - z is used to track a total value across the program
 *  - w is only used for reading input and copying it elsewhere
 *  - y is used for adding to and multiplying z
 *  - x is used for modifying y
 * With the variable behaviors in mind, the basic loop between inputs adds a new term to z like so:
 *  1. Read the input.
 *  1. Sometimes divide z by 26.
 *  1. Via some obfuscation, set x to a function of the last term in z: it will always resolve to 0 or 1.
 *     - Sometimes x is forced to be 1 (e.g. x=prev_input+15, then compare to w which is always in [0..9])
 *     - Other times we can find x = prev_input + some value in the range -8..8.
 *  1. If x==1, we multiply z by 26 and add the latest input plus some small amount (using y)
 *  1. Else x==0, so no new term is added to z.
 * So the way to get the program to output 0 is to provide inputs which force x==1. There are 7 points where
 * well-chosen inputs can force this -- which creates 7 pairs of input digits that must be a fixed amount
 * apart from one another (e.g. input4 = input3+4 yields x==0 in my code).
 * Finding minimum and maximum values is then just a matter of satisfying these pairs, fixing the min of
 * each pair to 1 or the max to 9 depending on whether you want the high or low value.
 */
fun main() {
  val program = File("src/adventofcode/2021/24/input.txt").readLines().map { l ->
    // Drop everything after '#' so we can annotate lines in the input.
    val ins = l.split(" #")[0].split(" ")
    if (ins.size == 2) Inp(ins[1])
    else {
      val a = ins[1]
      val b = ins[2]
      when (ins[0]) {
        "mul" -> Mul(a, b)
        "div" -> Div(a, b)
        "add" -> Add(a, b)
        "mod" -> Mod(a, b)
        "eql" -> Eql(a, b)
        else -> error("Invalid input")
      }
    }
  }
  val bottom = 17153114691118L
  println("$bottom is min: " + alu(program, toInput(bottom)!!))

  val top    = 29599469991739L
  println("$top is max: " + alu(program, toInput(top)!!))
}

fun toInput(x: Long): List<Long>? {
  val digits = x.toString().split("").filter { it.isNotBlank() }.map { it.toLong() }
  return if (digits.contains(0)) null else digits
}

sealed class Instruction
data class Inp(val v: String) : Instruction()
data class Add(val v1: String, val v2: String) : Instruction()
data class Mul(val v1: String, val v2: String) : Instruction()
data class Div(val v1: String, val v2: String) : Instruction()
data class Mod(val v1: String, val v2: String) : Instruction()
data class Eql(val v1: String, val v2: String) : Instruction()

fun alu(instructions: List<Instruction>, input: List<Long>): Long {
  val vars = mutableMapOf(Pair("w", 0L), Pair("x", 0L), Pair("y", 0L), Pair("z", 0L))
  var inputCount = 0
  for (i in instructions) {
    when (i) {
      is Inp -> {
        if (input.size == inputCount) {
          println("Out of input, breaking early")
          break
        }
        vars[i.v] = input[inputCount++]
      }
      is Add -> {
        vars[i.v1] = vars[i.v1]!! + (vars[i.v2] ?: i.v2.toLong())
      }
      is Mul -> {
        vars[i.v1] = vars[i.v1]!! * (vars[i.v2] ?: i.v2.toLong())
      }
      is Div -> {
        vars[i.v1] = vars[i.v1]!! / (vars[i.v2] ?: i.v2.toLong())
      }
      is Mod -> {
        vars[i.v1] = vars[i.v1]!! % (vars[i.v2] ?: i.v2.toLong())
      }
      is Eql -> {
        vars[i.v1] = if (vars[i.v1]!! == (vars[i.v2] ?: i.v2.toLong())) 1 else 0
      }
    }
  }
  return vars["z"]!!
}