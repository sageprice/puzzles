package adventofcode.`2020`.`21`

import java.io.File

fun main() {
  val recipes: List<Recipe> =
      File("src/adventofcode/2020/21/input.txt")
          .readLines()
          .map { parseIngredients(it) }

  // Part 1
  val allergenToIngredients = getAllergenToIngredients(recipes)
  val allergens: Set<String> = allergenToIngredients.values.toSet()
  val safeIngredientsCount =
      recipes
          .map { it.ings }
          .map { it.filterNot { ing -> allergens.contains(ing) } }
          .sumBy { it.size }
  println("Part 1: $safeIngredientsCount")

  // Part 2
  println(
      allergenToIngredients
          .toList()
          .sortedBy { it.first }
          .joinToString(",") { it.second })
}

private fun parseIngredients(recipe: String): Recipe {
  val parts = recipe.split(" (contains ")
  val ingredients = parts[0].split(" ")
  val allergens =
      parts[1]
          .substring(0 until parts[1].length - 1)
          .split(", ")
  return Recipe(ingredients, allergens)
}

private fun getAllergenToIngredients(recipes: List<Recipe>): Map<String, String> {
  val allergenToIngredients = mutableMapOf<String, MutableSet<String>>()
  for (recipe in recipes) {
    recipe.allergens.forEach { allergen ->
      allergenToIngredients[allergen] =
          if (allergenToIngredients.containsKey(allergen))
            allergenToIngredients[allergen]!!.intersect(recipe.ings).toMutableSet()
          else recipe.ings.toMutableSet()
    }
  }

  while (allergenToIngredients.values.any { it.size != 1 }) {
    for ((a: String, i: MutableSet<String>) in allergenToIngredients) {
      if (i.size == 1) {
        val uniqueAllergen = i.first()
        for ((a1, i1: MutableSet<String>) in allergenToIngredients) {
          if (a1 != a && i1.contains(uniqueAllergen)) {
            i1.remove(uniqueAllergen)
          }
        }
      }
    }
  }
  return allergenToIngredients.mapValues { (_,i) -> i.first() }
}

private data class Recipe(val ings: List<String>, val allergens: List<String>)