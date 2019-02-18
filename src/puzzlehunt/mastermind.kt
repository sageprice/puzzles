package puzzlehunt

fun main(args: Array<String>) {

// PUZZLE 1
//  val baseWords = listOf(
//      WordScore("BEYOND", 2, 0),
//      WordScore("BRUNCH", 0, 2),
//      WordScore("GYRATE", 1, 2),
//      WordScore("LAMENT", 1, 1),
//      WordScore("PARSED", 1, 1),
//      WordScore("REGAIN", 0, 3),
//      WordScore("SIMPLY", 1, 0))
// PUZZLE 2
//  val baseWords = listOf(
//      WordScore("BONHAM", 1, 2),
//      WordScore("ETHNIC", 2, 0),
//      WordScore("POETRY", 0, 4),
//      WordScore("RHOMBI", 1, 1),
//      WordScore("SPRING", 1, 1),
//      WordScore("STREAM", 1, 1))
// PUZZLE 3
//  val baseWords = listOf(
//      WordScore("ANGELO", 1, 2),
//      WordScore("BACKUP", 2, 1),
//      WordScore("ESCORT", 2, 0),
//      WordScore("IMPOSE", 0, 3),
//      WordScore("MISERY", 0, 2),
//      WordScore("PUNDIT", 0, 2),
//      WordScore("UNWARY", 2, 0))
// PUZZLE 4
  val baseWords = listOf(
      WordScore("BINGES", 0, 2),
      WordScore("CLOUDY", 0, 2),
      WordScore("MENSCH", 0, 2),
      WordScore("MOCKUP", 1, 1),
      WordScore("QUARTZ", 0, 3),
      WordScore("RENTAL", 2, 1),
      WordScore("TUBING", 0, 1))
// PUZZLE 5
//  val baseWords = listOf(
//      WordScore("BECALM", 1, 1),
//      WordScore("EQUITY", 2, 0),
//      WordScore("GENTLY", 0, 2),
//      WordScore("GROWTH", 2, 0),
//      WordScore("RUSTIC", 1, 2),
//      WordScore("SHADOW", 0, 2),
//      WordScore("SLEAZY", 1, 3))
// PUZZLE 6
//  val baseWords = listOf(
//      WordScore("CURATE", 2, 0),
//      WordScore("FRONDS", 1, 0),
//      WordScore("HAZING", 0, 4),
//      WordScore("ORIENT", 1, 2),
//      WordScore("STRIKE", 0, 2),
//      WordScore("ZURICH", 0, 4))
// PUZZLE 7
//  val baseWords = listOf(
//      WordScore("FIESTA", 0, 4),
//      WordScore("GOPHER", 0, 1),
//      WordScore("GOTHIC", 2, 0),
//      WordScore("INJURE", 1, 2),
//      WordScore("LOUNGE", 0, 3),
//      WordScore("WIZARD", 1, 1),
//      WordScore("ZODIAC", 0, 2))
// PUZZLE 8
//  val baseWords = listOf(
//      WordScore("CARBON", 1, 1),
//      WordScore("EMBARK", 0, 3),
//      WordScore("KINDLY", 2, 1),
//      WordScore("NATURE", 2, 0),
//      WordScore("PARKED", 0, 1))
// PUZZLE 9
//  val baseWords = listOf(
//      WordScore("CAMPUS", 1, 2),
//      WordScore("CHOSEN", 1, 1),
//      WordScore("CHUNKY", 2, 0),
//      WordScore("COMING", 0, 1),
//      WordScore("JASPER", 0, 1),
//      WordScore("NEARBY", 1, 0),
//      WordScore("POSTER", 0, 2),
//      WordScore("SOUGHT", 1, 3))

  // First approach: compare against internet-sourced list of words.
//  File("src/puzzlehunt/words_alpha.txt").forEachLine { l ->
//    if (l.length == 6 && baseWords.stream().allMatch { ws -> ws.plausiblyMatches(l.toUpperCase()) })
//      println(l.toUpperCase())
//  }

  // Second approach: generate all strings and check for validity incrementally.
  mastermind(6, RESTRICTED_ALPHABET, baseWords).forEach { l ->
    if (l.length == 6 && baseWords.stream().allMatch { ws -> ws.plausiblyMatches(l.toUpperCase()) })
      println(l.toUpperCase())
  }
}

class WordScore(private val word: String, private val exact: Int, private val misplaced: Int) {

  fun plausiblyMatches(other: String): Boolean {
    var exactCount = 0
    var misplacedCount = 0
    for (i in 0 until word.length) {
      if (word[i] == other[i])
        exactCount++
      else if (word.contains(other[i]))
        // maybe TODO: handle same letter appearing multiple times?
        misplacedCount++
    }
    return exactCount == exact && misplacedCount == misplaced
  }

  fun plausiblySubstring(other: String): Boolean {
    var exactCount = 0
    var misplacedCount = 0
    for (i in 0 until other.length) {
      if (word[i] == other[i])
        exactCount++
      else if (word.contains(other[i]))
        // maybe TODO: handle same letter appearing multiple times?
        misplacedCount++
    }
    return exactCount <= exact && misplacedCount <= misplaced
  }

}

// Alphabet consisting only of letters found in puzzle #4 words.
const val RESTRICTED_ALPHABET = "ABCDEGHIKLMNOPQRSTUYZ"

fun mastermind(l: Int, alphabet: String, words: List<WordScore>): List<String> {
  return mastermind(l, alphabet, listOf(""), words)
}

/** Solves a mastermind puzzle by generating all words of appropriate length. */
fun mastermind(l: Int, alphabet: String, bases: List<String>, words: List<WordScore>): List<String> {
  if (bases[0].length == l) {
    return bases
  }
  println("Generating words of length: " + bases[0].length)
  return mastermind(l,
      alphabet,
      bases.asSequence().map { w ->
        alphabet.asSequence().map { c -> w + c }.filter { nw ->
          words.stream().allMatch { ws ->
            ws.plausiblySubstring(nw)
          }
        }
      }.flatten().toList(),
      words)
}
