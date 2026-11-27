package com.example.cookbook.data.repository

import com.example.cookbook.data.api.RecipeApiService
import com.example.cookbook.data.local.RecipeDao
import com.example.cookbook.data.mapper.entitiesToDomain
import com.example.cookbook.data.mapper.toDomain
import com.example.cookbook.data.mapper.toEntities
import com.example.cookbook.data.mapper.toEntity
import com.example.cookbook.data.mapper.toRequestDto
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.model.ResourceResult
import com.example.cookbook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepositoryImpl @Inject constructor(
    private val recipeApiService: RecipeApiService,
    private val recipeDao: RecipeDao
) : RecipeRepository {

    override fun observeRecipes(): Flow<List<Recipe>> {
        return recipeDao.observeAll().map { entities ->
            entities.entitiesToDomain()
        }
    }

    override fun observeRecipeById(id: Int): Flow<Recipe?> {
        return recipeDao.observeById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun refresh(): ResourceResult<Unit> {
        return try {
            val remoteDtos = recipeApiService.getRecipes()
            val remoteEntities = remoteDtos.toEntities()

            recipeDao.upsertAll(remoteEntities)

            // Borra de Room las recetas que ya no vengan del servidor
            val remoteIds = remoteEntities.map { it.id }.toSet()
            val currentLocal = recipeDao.getAll()
            currentLocal.filter { it.id !in remoteIds }.forEach { obsolete ->
                recipeDao.deleteById(obsolete.id)
            }

            ResourceResult.Success(Unit)
        } catch (e: IOException) {
            ResourceResult.NetworkError("Sin conexión a internet. Mostrando recetas guardadas localmente.")
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> ResourceResult.Unauthorized
                else -> ResourceResult.ServerError(e.code(), parseHttpError(e))
            }
        } catch (e: Exception) {
            ResourceResult.UnknownError(e.message ?: "Error desconocido al actualizar recetas.")
        }
    }

    override suspend fun getRecipes(): Result<List<Recipe>> {
        refresh()
        val cached = recipeDao.getAll().entitiesToDomain()
        return Result.success(cached)
    }

    override suspend fun getRecipeById(id: Int): Result<Recipe> {
        return try {
            val remoteDto = recipeApiService.getRecipeById(id)
            val entity = remoteDto.toEntity()
            recipeDao.upsert(entity)
            Result.success(entity.toDomain())
        } catch (e: Exception) {
            val local = recipeDao.getById(id)
            if (local != null) {
                Result.success(local.toDomain())
            } else {
                val errorMsg = when (e) {
                    is IOException -> "Sin conexión a internet y no hay copia local de esta receta."
                    is HttpException -> parseHttpError(e)
                    else -> e.message ?: "Error al cargar la receta."
                }
                Result.failure(Exception(errorMsg))
            }
        }
    }

    override suspend fun createRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            val responseDto = recipeApiService.createRecipe(recipe.toRequestDto())
            val savedEntity = responseDto.toEntity()
            recipeDao.upsert(savedEntity)
            Result.success(savedEntity.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo guardar la receta en el servidor."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al crear la receta."))
        }
    }

    override suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe> {
        return try {
            val responseDto = recipeApiService.updateRecipe(id, recipe.toRequestDto())
            val updatedEntity = responseDto.toEntity()
            recipeDao.upsert(updatedEntity)
            Result.success(updatedEntity.toDomain())
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo actualizar la receta en el servidor."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al actualizar la receta."))
        }
    }

    override suspend fun deleteRecipe(id: Int): Result<Unit> {
        return try {
            val response = recipeApiService.deleteRecipe(id)
            if (response.isSuccessful) {
                recipeDao.deleteById(id)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se pudo eliminar la receta (Código ${response.code()})"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception(parseHttpError(e)))
        } catch (e: IOException) {
            Result.failure(Exception("Error de red. No se pudo eliminar la receta del servidor."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Error inesperado al eliminar la receta."))
        }
    }

    override suspend fun clearLocalData() {
        recipeDao.clear()
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
