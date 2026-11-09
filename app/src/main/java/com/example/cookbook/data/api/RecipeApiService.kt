package com.example.cookbook.data.api

import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.dto.RecipeRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecipeApiService {

    @GET("api/recetas/")
    suspend fun getRecipes(): List<RecipeDto>

    @GET("api/recetas/{receta_id}")
    suspend fun getRecipeById(
        @Path("receta_id") recipeId: Int
    ): RecipeDto

    @POST("api/recetas/")
    suspend fun createRecipe(
        @Body recipe: RecipeRequestDto
    ): RecipeDto

    @PUT("api/recetas/{receta_id}")
    suspend fun updateRecipe(
        @Path("receta_id") recipeId: Int,
        @Body recipe: RecipeRequestDto
    ): RecipeDto

    @DELETE("api/recetas/{receta_id}")
    suspend fun deleteRecipe(
        @Path("receta_id") recipeId: Int
    ): Response<Unit>
}
