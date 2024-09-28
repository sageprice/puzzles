package adventofcode.`2015`.`21`

import java.io.File
import kotlin.math.max
import kotlin.math.min

fun main() {
  val input = File("src/adventofcode/2015/21/input.txt").readLines()
  val hp = input[0].split(": ").last().toInt()
  val damage = input[1].split(": ").last().toInt()
  val defense = input[2].split(": ").last().toInt()
  val boss = Boss(hp, damage, defense)

  val weapons = listOf(
    Equipment("Dagger", 8, 4, 0),
    Equipment("Shortsword", 10, 5, 0),
    Equipment("Warhammer", 25, 6, 0),
    Equipment("Longsword", 40, 7, 0),
    Equipment("Greataxe", 74, 8, 0)
  )

  val armor = listOf(
    Equipment("Leather", 13, 0, 1),
    Equipment("Chainmail", 31,  0, 2),
    Equipment("Splintmail", 53, 0, 3),
    Equipment("Bandedmail", 75, 0, 4),
    Equipment("Platemail", 102, 0, 5)
  )

  val rings = listOf(
    Equipment("Dam+1", 25, 1, 0),
    Equipment("Dam+2", 50,  2, 0),
    Equipment("Dam+3", 100, 3, 0),
    Equipment("Def+1", 20, 0, 1),
    Equipment("Def+2", 40, 0, 2),
    Equipment("Def+3", 40, 0, 3)
  )

  println(getCheapestWin(boss, weapons, armor, rings))
  println(getMostExpensiveLoss(boss, weapons, armor, rings))
}

private fun getCheapestWin(boss: Boss, weapons: List<Equipment>, armor: List<Equipment>, rings: List<Equipment>): Int {
  val loadouts = generateLoadouts(weapons, armor, rings)
  var minCost = Int.MAX_VALUE
  loadouts.forEach { l ->
    val player = Player(100, 0, 0, l)
    if (fightBoss(player, boss)) {
      minCost = min(player.getCost(), minCost)
    }
  }
  return minCost
}

private fun getMostExpensiveLoss(boss: Boss, weapons: List<Equipment>, armor: List<Equipment>, rings: List<Equipment>): Int {
  val loadouts = generateLoadouts(weapons, armor, rings)
  var maxCost = Int.MIN_VALUE
  loadouts.forEach { l ->
    val player = Player(100, 0, 0, l)
    if (!fightBoss(player, boss)) {
      maxCost = max(player.getCost(), maxCost)
    }
  }
  return maxCost
}

private fun fightBoss(player: Player, boss: Boss): Boolean {
  var myHp = player.hp
  var bossHp = boss.hp
  while (myHp > 0) {
    val d = max(player.getAttack() - boss.def, 1)
    bossHp -= d
    if (bossHp <= 0) return true
    val p = max(boss.atk - player.getDefense(), 1)
    myHp -= p
  }
  return false
}

private fun generateLoadouts(weapons: List<Equipment>, armor: List<Equipment>, rings: List<Equipment>): List<List<Equipment>> {
  val weaponChoices: List<List<Equipment>> = weapons.map { listOf(it) }
  val armorChoices: List<List<Equipment>> = armor.map { listOf(it) } + listOf(emptyList())
  // Leads to duplication, but small number of possibilities so who cares.
  val ringChoices: List<List<Equipment>> =
    rings.map { setOf(it) }
      .flatMap { rs -> rings.map { r -> (rs + r).toList() } } + listOf(emptyList())
  val loadouts = mutableListOf<List<Equipment>>()
  for (w in weaponChoices) for (a in armorChoices) for (rs in ringChoices) {
    loadouts.add(w + a + rs)
  }
  return loadouts
}

private data class Player(
  val hp: Int,
  val atk: Int,
  val def: Int,
  val equip: List<Equipment>
) {
  fun getAttack(): Int = atk + equip.sumOf { it.damage }
  fun getDefense(): Int = def + equip.sumOf { it.armor }
  fun getCost(): Int = equip.sumOf { it.cost }
}

private data class Boss(
  val hp: Int,
  val atk: Int,
  val def: Int,
)

private data class Equipment(
  val name: String,
  val cost: Int,
  val damage: Int,
  val armor: Int
)
