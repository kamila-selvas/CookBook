package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.repository.RecipeRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val recipeRepository: RecipeRepository
) {
    suspend operator fun invoke() {
        authRepository.logout()
        recipeRepository.clearLocalData()
    }
}
