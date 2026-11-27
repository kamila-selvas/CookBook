package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRandomRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): Result<Recipe?> {
        val result = recipeRepository.getRecipes()
        return result.map { list ->
            if (list.isEmpty()) null else list.random()
        }
    }
}
