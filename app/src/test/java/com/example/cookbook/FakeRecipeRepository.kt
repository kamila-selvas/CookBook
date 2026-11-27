package com.example.cookbook

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.model.ResourceResult
import com.example.cookbook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeRecipeRepository : RecipeRepository {

    private val _recipesFlow = MutableStateFlow<List<Recipe>>(emptyList())
    var shouldFailRefresh: Boolean = false
    var refreshResultToReturn: ResourceResult<Unit>? = null
    var clearLocalDataCallCount: Int = 0

    var recipesToReturn: List<Recipe>
        get() = _recipesFlow.value
        set(value) {
            _recipesFlow.value = value
        }

    override fun observeRecipes(): Flow<List<Recipe>> = _recipesFlow.asStateFlow()

    override fun observeRecipeById(id: Int): Flow<Recipe?> {
        return _recipesFlow.map { list -> list.find { it.id == id } }
    }

    override suspend fun refresh(): ResourceResult<Unit> {
        refreshResultToReturn?.let { return it }
        return if (shouldFailRefresh) {
            ResourceResult.NetworkError("Error simulado de red en refresh")
        } else {
            ResourceResult.Success(Unit)
        }
    }

    override suspend fun getRecipes(): Result<List<Recipe>> {
        return Result.success(_recipesFlow.value)
    }

    override suspend fun getRecipeById(id: Int): Result<Recipe> {
        val recipe = _recipesFlow.value.find { it.id == id }
        return if (recipe != null) {
            Result.success(recipe)
        } else {
            Result.failure(Exception("Receta no encontrada"))
        }
    }

    override suspend fun createRecipe(recipe: Recipe): Result<Recipe> {
        _recipesFlow.value = _recipesFlow.value + recipe
        return Result.success(recipe)
    }

    override suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe> {
        _recipesFlow.value = _recipesFlow.value.map { if (it.id == id) recipe else it }
        return Result.success(recipe)
    }

    override suspend fun deleteRecipe(id: Int): Result<Unit> {
        _recipesFlow.value = _recipesFlow.value.filterNot { it.id == id }
        return Result.success(Unit)
    }

    override suspend fun clearLocalData() {
        clearLocalDataCallCount++
        _recipesFlow.value = emptyList()
    }
}
