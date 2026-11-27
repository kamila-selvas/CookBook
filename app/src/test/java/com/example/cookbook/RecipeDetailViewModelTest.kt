package com.example.cookbook

import androidx.lifecycle.SavedStateHandle
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.DeleteRecipeUseCase
import com.example.cookbook.domain.usecase.GetRecipeByIdUseCase
import com.example.cookbook.domain.usecase.MarkFavoriteRecipeUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.ui.recipes.RecipeDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRecipeRepo: FakeRecipeRepository
    private lateinit var fakeHapticRepo: FakeHapticRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeRepo = FakeRecipeRepository().apply {
            recipesToReturn = listOf(
                Recipe(
                    id = 42,
                    nombre = "Pizza Napolitana",
                    ingredientes = "Harina, Levadura, Tomate, Mozzarella",
                    instrucciones = "Hornear a alta temperatura",
                    esFavorita = false
                )
            )
        }
        fakeHapticRepo = FakeHapticRepository(isAvailable = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleFavorite_updatesState_andTriggersLightClickHaptic() = runTest {
        val getRecipeByIdUseCase = GetRecipeByIdUseCase(fakeRecipeRepo)
        val deleteRecipeUseCase = DeleteRecipeUseCase(fakeRecipeRepo)
        val markFavoriteRecipeUseCase = MarkFavoriteRecipeUseCase(fakeRecipeRepo)
        val performHapticUseCase = PerformHapticUseCase(fakeHapticRepo)

        val viewModel = RecipeDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("recipeId" to 42)),
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            deleteRecipeUseCase = deleteRecipeUseCase,
            markFavoriteRecipeUseCase = markFavoriteRecipeUseCase,
            performHapticUseCase = performHapticUseCase
        )

        runCurrent()
        assertEquals("Pizza Napolitana", viewModel.uiState.value.recipe?.nombre)
        assertEquals(false, viewModel.uiState.value.recipe?.esFavorita)

        fakeHapticRepo.clear()

        // Toggle a favorita = true
        viewModel.toggleFavorite()
        runCurrent()

        assertTrue(viewModel.uiState.value.recipe?.esFavorita == true)
        assertEquals(1, fakeHapticRepo.performedEffects.count { it == HapticEffect.LIGHT_CLICK })
    }
}
