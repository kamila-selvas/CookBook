package com.example.cookbook

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import com.example.cookbook.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeRecipeRepository : RecipeRepository {
    var recipesToReturn: List<Recipe> = emptyList()

    override suspend fun getRecipes(): Result<List<Recipe>> {
        return Result.success(recipesToReturn)
    }

    override suspend fun getRecipeById(id: Int): Result<Recipe> {
        val recipe = recipesToReturn.find { it.id == id }
        return if (recipe != null) Result.success(recipe) else Result.failure(Exception("Receta no encontrada"))
    }

    override suspend fun createRecipe(recipe: Recipe): Result<Recipe> = Result.success(recipe)
    override suspend fun updateRecipe(id: Int, recipe: Recipe): Result<Recipe> = Result.success(recipe)
    override suspend fun deleteRecipe(id: Int): Result<Unit> = Result.success(Unit)
}

class GetRecipesUseCaseTest {

    @Test
    fun getRecipesUseCase_returnsRecipesFromRepository() = runBlocking {
        val fakeRepo = FakeRecipeRepository()
        fakeRepo.recipesToReturn = listOf(
            Recipe(
                id = 1,
                nombre = "Tacos al Pastor",
                ingredientes = "Tortillas, Carne de cerdo, Piña",
                instrucciones = "Marinar la carne y cocinar al pastor"
            )
        )

        val useCase = GetRecipesUseCase(fakeRepo)
        val result = useCase()

        assertTrue(result.isSuccess)
        val list = result.getOrNull()
        assertEquals(1, list?.size)
        assertEquals("Tacos al Pastor", list?.first()?.nombre)
    }
}
