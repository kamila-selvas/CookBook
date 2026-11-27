package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class DeleteRecipeUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return recipeRepository.deleteRecipe(id)
    }
}
