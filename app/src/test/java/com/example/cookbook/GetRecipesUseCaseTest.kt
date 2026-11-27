package com.example.cookbook

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRecipesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
