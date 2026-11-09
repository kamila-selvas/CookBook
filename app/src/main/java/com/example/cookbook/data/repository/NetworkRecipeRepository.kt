package com.example.cookbook.data.repository

import com.example.cookbook.data.api.RecipeApiService
import com.example.cookbook.data.mapper.toDomain
import com.example.cookbook.data.mapper.toRequestDto
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import retrofit2.HttpException
import java.io.IOException

class NetworkRecipeRepository(
    private val recipeApiService: RecipeApiService
) : RecipeRepository {

    override suspend fun getRecipes(): Result<List<Recipe>> {
        return try {
            val response = recipeApiService.getRecipes()
            Result.success(response.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudieron obtener las recetas."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al cargar las recetas."))
        }
    }

    override suspend fun getRecipeById(id: Int): Result<Recipe> {
        return try {
            val response = recipeApiService.getRecipeById(id)
            Result.success(response.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo obtener el detalle de la receta."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al cargar la receta."))
        }
    }

    override suspend fun createRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val response = recipeApiService.createRecipe(recipe.toRequestDto())
            Result.success(response.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo guardar la receta."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al crear la receta."))
        }
    }

    override suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe> {
        return try {
            val response = recipeApiService.updateRecipe(id, recipe.toRequestDto())
            Result.success(response.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo actualizar la receta."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al actualizar la receta."))
        }
    }

    override suspend fun deleteRecipe(id: Int): Result<Unit> {
        return try {
            val response = recipeApiService.deleteRecipe(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar la receta (Código ${response.code()})"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo eliminar la receta."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al eliminar la receta."))
        }
    }

    private fun parseHttpError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank() && errorBody.contains("detail")) {
                val detailPattern = """"detail"\s*:\s*"([^"]+)"""".toRegex()
                val match = detailPattern.find(errorBody)
                if (match != null) {
                    return match.groupValues[1]
                }
            }
            when (e.code()) {
                401 -> "Sesión expirada o no autorizada. Por favor vuelve a iniciar sesión."
                403 -> "No tienes permisos para realizar esta acción."
                404 -> "Receta no encontrada."
                422 -> "Datos de receta inválidos."
                500 -> "Error en el servidor de recetas."
                else -> "Error en la petición de recetas (Código ${e.code()})"
            }
        } catch (_: Exception) {
            "Error de comunicación con el servidor (Código ${e.code()})"
        }
    }
}
