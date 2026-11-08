package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.Recipe

/**
 * Contrato de repositorio para la gestión de recetas.
 */
interface RecipeRepository {
    /**
     * Obtiene el listado completo de recetas desde la API.
     */
    suspend fun getRecipes(): Result<List<Recipe>>

    /**
     * Obtiene una receta específica mediante su ID.
     */
    suspend fun getRecipeById(id: Int): Result<Recipe>

    /**
     * Crea una nueva receta en el servidor.
     */
    suspend fun createRecipe(recipe: Recipe): Result<Recipe>

    /**
     * Actualiza una receta existente por su ID.
     */
    suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe>

    /**
     * Elimina una receta del servidor por su ID.
     */
    suspend fun deleteRecipe(id: Int): Result<Unit>
}
