package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRecipeByIdUseCase @Inject constructor(
    private val recipeRepository: RecipeRepository
) {
    operator fun invoke(id: Int): Flow<Recipe?> {
        return recipeRepository.observeRecipeById(id)
    }
}
