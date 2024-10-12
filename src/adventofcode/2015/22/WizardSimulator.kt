package adventofcode.`2015`.`22`

import java.io.File
import java.util.PriorityQueue
import kotlin.math.max

fun main() {
  val input = File("src/adventofcode/2015/22/input.txt").readLines().map { it.split(": ") }
  val bossHp = input.first().last().toInt()
  val bossDamage = input.last().last().toInt()

  println(findCheapestWin(bossHp, bossDamage, 0))
  println(findCheapestWin(bossHp, bossDamage, 1))
}

private data class GameState(
  val bossHp: Int,
  val damage: Int,
  val playerHp: Int,
  val mana: Int,
  val effects: Map<String, Int>,
  val isPlayerTurn: Boolean,
  val manaSpent: Int
)

private fun findCheapestWin(
  bossHp: Int,
  bossDamage: Int,
  playerPoisonDmg: Int
): Int {
  // Use a heap so that we're always searching the cheapest paths first.
  val states = PriorityQueue(Comparator.comparing { gs: GameState -> gs.manaSpent }.reversed())
  states.add(GameState(bossHp, bossDamage, 50, 500, emptyMap(), true, 0))
  var best = Int.MAX_VALUE
  while (states.isNotEmpty()) {
    var (bhp, bd, hp, m, e, isPt, ms) = states.remove()
    // Once we've found a good path, we can prune all the more expensive ones.
    if (ms > best) continue
    val newEffects = e.toMutableMap()
    if (bhp <= 0) return ms
    var def = 0
    for ((spell, _) in e) {
      when (spell) {
        "Shield" -> def = 7
        "Poison" -> {
          if (bhp <= 3) {
            if (ms < best) best = ms
            continue
          }
          bhp -= 3
        }
        "Recharge" -> m += 101
      }
      newEffects[spell] = newEffects[spell]!! - 1
    }
    val done = newEffects.filter { (_, t) -> t == 0 }
    done.forEach { (s, _) -> newEffects.remove(s)}
    if (isPt) {
      hp -= playerPoisonDmg
      if (hp <= 0) continue
      // Filtering assumption: it is always optimal to do something on our turn. Skip the 'no action' case.
      spellCosts.filter { (s, c) -> c <= m && s !in newEffects }.forEach { (spell, cost) ->
        when (spell) {
          "MM" -> {
            val newMs = ms + cost
            if (bhp <= 4) {
              if (newMs < best) best = newMs
            } else states.add(GameState(bhp - 4, bd, hp, m - cost, newEffects, false, newMs))
          }
          "Drain" -> {
            val newMs = ms + cost
            if (bhp <= 2) {
              if (newMs < best) best = newMs
            } else states.add(GameState(bhp - 2, bd, hp + 2, m - cost, newEffects, false, newMs))
          }
          "Shield" -> {
            val ne = newEffects.toMutableMap()
            ne[spell] = 6
            states.add(GameState(bhp, bd, hp, m - cost, ne, false, ms + cost))
          }
          "Poison" -> {
            val ne = newEffects.toMutableMap()
            ne[spell] = 6
            states.add(GameState(bhp, bd, hp, m - cost, ne, false, ms + cost))
          }
          "Recharge" -> {
            val ne = newEffects.toMutableMap()
            ne[spell] = 5
            states.add(GameState(bhp, bd, hp, m - cost, ne, false, ms + cost))
          }
        }
      }
    } else {
      val newHp = hp - max(1, bd - def)
      if (newHp >= 0) {
        states.add(GameState(bhp, bossDamage, newHp, m, newEffects, true, ms))
      }
    }
  }
  return best
}

private val spellCosts = listOf(
  "MM" to 53,
  "Drain" to 73,
  "Shield" to 113,
  "Poison" to 173,
  "Recharge" to 229
)
