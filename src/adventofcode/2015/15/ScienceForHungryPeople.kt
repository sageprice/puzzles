package adventofcode.`2015`.`15`

import java.io.File

fun main() {
  val ingredients = File("src/adventofcode/2015/15/input.txt").readLines().map { parseIngredient(it) }
  println(maximizeCookies(ingredients, 100))
  println(maximizeCookiesWithCalories(ingredients, 100, 500))
}

private fun maximizeCookies(ingredients: List<Ingredient>, allowed: Int): Long {
  val counts = MutableList(ingredients.size) { 1 }
  for (i in counts.size+1..allowed) {
    val indexToIncrement = List(ingredients.size) { idx ->
      idx to getScore(ingredients, counts.mapIndexed { index, count -> if (index == idx) count+1 else count })
    }.maxBy { it.second }.first
    counts[indexToIncrement] = 1 + counts[indexToIncrement]
  }
  return getScore(ingredients, counts)
}

private fun maximizeCookiesWithCalories(ingredients: List<Ingredient>, allowed: Int, calories: Long): Long {
  val recipes = getIngredientCombinations(ingredients.size, allowed)
  val validRecipes = recipes.filter { getCalories(ingredients, it) == calories }
  return validRecipes.maxOf { getScore(ingredients, it) }
}

private fun getIngredientCombinations(ingredients: Int, available: Int): List<List<Int>> {
  if (available == 0 && ingredients > 0) return listOf(List(ingredients) { 0 })
  if (ingredients == 1) return listOf(listOf(available))
  return (0..available).flatMap { i ->
    getIngredientCombinations(ingredients - 1, available - i).map {
      buildList {
        add(i)
        addAll(it)
      }
    }
  }
}

private fun getCalories(ingredients: List<Ingredient>, recipe: List<Int>): Long {
  return ingredients.zip(recipe).sumOf { (ingredient, count) -> ingredient.calories * count }
}

private fun getScore(ingredients: List<Ingredient>, counts: List<Int>): Long {
  val ingCounts = ingredients.zip(counts)
  val capacity = ingCounts.sumOf { (ing, count) -> ing.capacity * count }
  val durability = ingCounts.sumOf { (ing, count) -> ing.durability * count }
  val flavor = ingCounts.sumOf { (ing, count) -> ing.flavor * count }
  val texture = ingCounts.sumOf { (ing, count) -> ing.texture * count }
  return capacity * durability * flavor * texture
}

private val INGREDIENT_REGEX = Regex(
  "([A-Za-z]+): capacity ([\\-\\d]+), durability ([\\-\\d]+), flavor ([\\-\\d]+), texture ([\\-\\d]+), calories ([\\-\\d]+)"
)

private fun parseIngredient(line: String): Ingredient {
  val groups = INGREDIENT_REGEX.matchEntire(line)!!.groupValues
  return Ingredient(
    name = groups[1],
    capacity = groups[2].toLong(),
    durability = groups[3].toLong(),
    flavor = groups[4].toLong(),
    texture = groups[5].toLong(),
    calories = groups[6].toLong()
  )
}

private data class Ingredient(
  val name: String,
  val capacity: Long,
  val durability: Long,
  val flavor: Long,
  val texture: Long,
  val calories: Long
)
