package com.example.cookbook

import com.example.cookbook.data.api.RecipeApiService
import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.dto.RecipeRequestDto
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class FakeRecipeApiService : RecipeApiService {
    val remoteRecipes: MutableList<RecipeDto> = mutableListOf()
    var shouldThrowNetworkError: Boolean = false
    var shouldThrowHttpError: Int? = null

    override suspend fun getRecipes(): List<RecipeDto> {
        if (shouldThrowNetworkError) {
            throw IOException("Network unreachable")
        }
        shouldThrowHttpError?.let { code ->
            val errorResponse = Response.error<List<RecipeDto>>(code, "{}".toResponseBody(null))
            throw HttpException(errorResponse)
        }
        return remoteRecipes
    }

    override suspend fun getRecipeById(recipeId: Int): RecipeDto {
        if (shouldThrowNetworkError) {
            throw IOException("Network unreachable")
        }
        return remoteRecipes.find { it.id == recipeId } ?: throw IOException("Not found")
    }

    override suspend fun createRecipe(recipe: RecipeRequestDto): RecipeDto {
        val newId = (remoteRecipes.maxOfOrNull { it.id } ?: 0) + 1
        val created = RecipeDto(
            id = newId,
            nombre = recipe.nombre,
            descripcion = recipe.descripcion,
            ingredientes = recipe.ingredientes,
            instrucciones = recipe.instrucciones,
            categoria = recipe.categoria,
            imagenUrl = recipe.imagenUrl,
            tiempoPreparacion = recipe.tiempoPreparacion,
            porciones = recipe.porciones,
            esFavorita = recipe.esFavorita
        )
        remoteRecipes.add(created)
        return created
    }

    override suspend fun updateRecipe(recipeId: Int, recipe: RecipeRequestDto): RecipeDto {
        val index = remoteRecipes.indexOfFirst { it.id == recipeId }
        val updated = RecipeDto(
            id = recipeId,
            nombre = recipe.nombre,
            descripcion = recipe.descripcion,
            ingredientes = recipe.ingredientes,
            instrucciones = recipe.instrucciones,
            categoria = recipe.categoria,
            imagenUrl = recipe.imagenUrl,
            tiempoPreparacion = recipe.tiempoPreparacion,
            porciones = recipe.porciones,
            esFavorita = recipe.esFavorita
        )
        if (index >= 0) {
            remoteRecipes[index] = updated
        } else {
            remoteRecipes.add(updated)
        }
        return updated
    }

    override suspend fun deleteRecipe(recipeId: Int): Response<Unit> {
        remoteRecipes.removeAll { it.id == recipeId }
        return Response.success(Unit)
    }
}
