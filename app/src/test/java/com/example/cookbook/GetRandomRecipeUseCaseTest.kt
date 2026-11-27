package com.example.cookbook

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRandomRecipeUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetRandomRecipeUseCaseTest {

    @Test
    fun getRandomRecipe_returnsExistingRecipe_whenListIsNotEmpty() = runTest {
        val recipe1 = Recipe(id = 1, nombre = "Guacamole", ingredientes = "Aguacate, Limón", instrucciones = "Moler")
        val recipe2 = Recipe(id = 2, nombre = "Chiles Rellenos", ingredientes = "Chile poblano, Queso", instrucciones = "Rellenar y capear")

        val fakeRepo = FakeRecipeRepository().apply {
            recipesToReturn = listOf(recipe1, recipe2)
        }

        val useCase = GetRandomRecipeUseCase(fakeRepo)
        val result = useCase()

        assertTrue(result.isSuccess)
        val selected = result.getOrNull()
        assertNotNull(selected)
        assertTrue(selected?.id == 1 || selected?.id == 2)
    }

    @Test
    fun getRandomRecipe_returnsNull_whenListIsEmpty() = runTest {
        val fakeRepo = FakeRecipeRepository().apply {
            recipesToReturn = emptyList()
        }

        val useCase = GetRandomRecipeUseCase(fakeRepo)
        val result = useCase()

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }
}
