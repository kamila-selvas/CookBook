package com.example.cookbook

import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRandomRecipeUseCase
import com.example.cookbook.domain.usecase.LogoutUseCase
import com.example.cookbook.domain.usecase.ObserveRecipesUseCase
import com.example.cookbook.domain.usecase.ObserveShakeUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.domain.usecase.RefreshRecipesUseCase
import com.example.cookbook.ui.recipes.RecipeListEvent
import com.example.cookbook.ui.recipes.RecipeListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelShakeTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRecipeRepo: FakeRecipeRepository
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeHapticRepo: FakeHapticRepository
    private lateinit var fakeShakeRepo: FakeShakeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeRepo = FakeRecipeRepository()
        fakeAuthRepo = FakeAuthRepository()
        fakeHapticRepo = FakeHapticRepository()
        fakeShakeRepo = FakeShakeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        recipes: List<Recipe> = emptyList(),
        shakeAvailable: Boolean = true
    ): RecipeListViewModel {
        fakeRecipeRepo.recipesToReturn = recipes
        fakeShakeRepo.isAvailable = shakeAvailable

        val observeRecipesUseCase = ObserveRecipesUseCase(fakeRecipeRepo)
        val refreshRecipesUseCase = RefreshRecipesUseCase(fakeRecipeRepo)
        val logoutUseCase = LogoutUseCase(fakeAuthRepo, fakeRecipeRepo)
        val getRandomRecipeUseCase = GetRandomRecipeUseCase(fakeRecipeRepo)
        val observeShakeUseCase = ObserveShakeUseCase(fakeShakeRepo)
        val performHapticUseCase = PerformHapticUseCase(fakeHapticRepo)

        return RecipeListViewModel(
            observeRecipesUseCase = observeRecipesUseCase,
            refreshRecipesUseCase = refreshRecipesUseCase,
            logoutUseCase = logoutUseCase,
            getRandomRecipeUseCase = getRandomRecipeUseCase,
            observeShakeUseCase = observeShakeUseCase,
            performHapticUseCase = performHapticUseCase
        )
    }

    @Test
    fun shake_producesNavigationEvent_withExistingRecipe_andHapticSuccess() = runTest {
        val recipes = listOf(
            Recipe(id = 101, nombre = "Enchiladas Verdes", ingredientes = "Tortilla, Pollo, Salsa", instrucciones = "Bañar en salsa"),
            Recipe(id = 102, nombre = "Pozole Rojo", ingredientes = "Maíz, Carne, Chile", instrucciones = "Hervir")
        )
        val viewModel = createViewModel(recipes = recipes)
        runCurrent()

        val emittedEvents = mutableListOf<RecipeListEvent>()
        val job = launch {
            viewModel.events.collect { emittedEvents.add(it) }
        }

        fakeHapticRepo.clear()

        // Simula la acción generada por una agitación o el botón "¿Qué cocino hoy?"
        viewModel.onRandomRecipeRequested()
        runCurrent()

        assertEquals(1, emittedEvents.size)
        val event = emittedEvents.first()
        assertTrue(event is RecipeListEvent.NavigateToRecipe)
        val navEvent = event as RecipeListEvent.NavigateToRecipe
        assertTrue(navEvent.recipeId == 101 || navEvent.recipeId == 102)

        // Verifica que se disparó háptica de SUCCESS
        assertEquals(1, fakeHapticRepo.performedEffects.count { it == HapticEffect.SUCCESS })

        job.cancel()
    }

    @Test
    fun shake_withNoRecipes_producesShowMessageEvent() = runTest {
        val viewModel = createViewModel(recipes = emptyList())
        runCurrent()

        val emittedEvents = mutableListOf<RecipeListEvent>()
        val job = launch {
            viewModel.events.collect { emittedEvents.add(it) }
        }

        viewModel.onRandomRecipeRequested()
        runCurrent()

        assertEquals(1, emittedEvents.size)
        val event = emittedEvents.first()
        assertTrue(event is RecipeListEvent.ShowMessage)
        val msgEvent = event as RecipeListEvent.ShowMessage
        assertEquals("No hay recetas disponibles para descubrir.", msgEvent.message)
        assertEquals("No hay recetas disponibles para descubrir.", viewModel.uiState.value.randomRecipeMessage)

        job.cancel()
    }

    @Test
    fun whenShakeSensorUnavailable_uiStateReflectsIt() = runTest {
        val viewModel = createViewModel(shakeAvailable = false)
        runCurrent()

        assertFalse(viewModel.uiState.value.isShakeAvailable)
    }
}
