package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class MarkFavoriteRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(recipe: Recipe, isFavorite: Boolean = true): Result<Recipe> {
        return recipeRepository.updateRecipe(recipe.id, recipe.copy(esFavorita = isFavorite))
    }

    suspend operator fun invoke(id: Int, recipe: Recipe, isFavorite: Boolean = true): Result<Recipe> {
        return recipeRepository.updateRecipe(id, recipe.copy(esFavorita = isFavorite))
    }
}
