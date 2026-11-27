package com.example.cookbook

import com.example.cookbook.data.local.RecipeDao
import com.example.cookbook.data.local.RecipeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeRecipeDao : RecipeDao {
    private val entitiesFlow = MutableStateFlow<Map<Int, RecipeEntity>>(emptyMap())

    override fun observeAll(): Flow<List<RecipeEntity>> {
        return entitiesFlow.asStateFlow().map { it.values.toList().sortedByDescending { r -> r.id } }
    }

    override fun observeById(id: Int): Flow<RecipeEntity?> {
        return entitiesFlow.asStateFlow().map { it[id] }
    }

    override suspend fun getById(id: Int): RecipeEntity? {
        return entitiesFlow.value[id]
    }

    override suspend fun getAll(): List<RecipeEntity> {
        return entitiesFlow.value.values.toList()
    }

    override suspend fun upsertAll(recipes: List<RecipeEntity>) {
        val current = entitiesFlow.value.toMutableMap()
        recipes.forEach { current[it.id] = it }
        entitiesFlow.value = current
    }

    override suspend fun upsert(recipe: RecipeEntity) {
        val current = entitiesFlow.value.toMutableMap()
        current[recipe.id] = recipe
        entitiesFlow.value = current
    }

    override suspend fun deleteById(id: Int) {
        val current = entitiesFlow.value.toMutableMap()
        current.remove(id)
        entitiesFlow.value = current
    }

    override suspend fun clear() {
        entitiesFlow.value = emptyMap()
    }
}
