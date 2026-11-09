package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.Recipe

interface RecipeRepository {
    suspend fun getRecipes(): Result<List<Recipe>>

    suspend fun getRecipeById(id: Int): Result<Recipe>

    suspend fun createRecipe(recipe: Recipe): Result<Recipe>

    suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe>

    suspend fun deleteRecipe(id: Int): Result<Unit>
}
