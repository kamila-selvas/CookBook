package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.ResourceResult
import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class RefreshRecipesUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke(): ResourceResult<Unit> {
        return recipeRepository.refresh()
    }
}
