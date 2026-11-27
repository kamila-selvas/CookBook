package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.model.ResourceResult
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun observeRecipes(): Flow<List<Recipe>>

    fun observeRecipeById(id: Int): Flow<Recipe?>

    suspend fun refresh(): ResourceResult<Unit>

    suspend fun getRecipes(): Result<List<Recipe>>

    suspend fun getRecipeById(id: Int): Result<Recipe>

    suspend fun createRecipe(recipe: Recipe): Result<Recipe>

    suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe>

    suspend fun deleteRecipe(id: Int): Result<Unit>

    suspend fun clearLocalData()
}
