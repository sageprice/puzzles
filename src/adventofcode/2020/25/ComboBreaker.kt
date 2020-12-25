package adventofcode.`2020`.`25`

fun main() {
  val cardKey = 2084668L
//  val cardKey = 5764801L
  val doorKey = 3704642L
//  val doorKey = 17807724L
  val mod = 20201227L

  var encryptionKey = 1L
  repeat(getLoopSize(doorKey, mod)) {
    encryptionKey *= cardKey
    encryptionKey %= mod
  }
  println("Final answer: $encryptionKey")
}

fun getLoopSize(publicKey: Long, mod: Long, subjectNumber: Long = 7L): Int {
  var value = 1L
  var loops = 0
  while (value != publicKey) {
    value *= subjectNumber
    value %= mod
    loops++
  }
  return loops
}