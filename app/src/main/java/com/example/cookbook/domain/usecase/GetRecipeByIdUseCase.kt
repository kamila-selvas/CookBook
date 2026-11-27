package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class GetRecipeByIdUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(id: Int): Result<Recipe> {
        return recipeRepository.getRecipeById(id)
    }
}
