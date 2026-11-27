package com.example.cookbook

import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRandomRecipeUseCase
import com.example.cookbook.domain.usecase.LogoutUseCase
import com.example.cookbook.domain.usecase.ObserveRecipesUseCase
import com.example.cookbook.domain.usecase.ObserveShakeUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.domain.usecase.RefreshRecipesUseCase
import com.example.cookbook.ui.recipes.RecipeListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeListViewModelOfflineTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRecipeRepo: FakeRecipeRepository
    private lateinit var fakeAuthRepo: FakeAuthRepository
    private lateinit var fakeShakeRepo: FakeShakeRepository
    private lateinit var fakeHapticRepo: FakeHapticRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeRepo = FakeRecipeRepository()
        fakeAuthRepo = FakeAuthRepository()
        fakeShakeRepo = FakeShakeRepository()
        fakeHapticRepo = FakeHapticRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): RecipeListViewModel {
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
    fun `when cache has recipes and network refresh fails uiState shows recipes with offline warning`() = runTest {
        // Precargamos datos en el Fake (simulando que Room ya tiene recetas guardadas)
        fakeRecipeRepo.recipesToReturn = listOf(
            Recipe(id = 1, nombre = "Chilaquiles Rojos", ingredientes = "Totopos, Salsa, Queso", instrucciones = "Bañar y servir")
        )
        fakeRecipeRepo.shouldFailRefresh = true

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertEquals(1, state.recipes.size)
        assertEquals("Chilaquiles Rojos", state.recipes.first().nombre)
        assertTrue(state.isOfflineWarning)
        assertNull(state.errorMessage)
    }

    @Test
    fun `when cache is empty and network refresh fails uiState transitions to full error state with retry`() = runTest {
        fakeRecipeRepo.recipesToReturn = emptyList()
        fakeRecipeRepo.shouldFailRefresh = true

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertEquals(0, state.recipes.size)
        assertFalse(state.isOfflineWarning)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `when cache is empty and network refresh succeeds with empty list uiState transitions to empty content state`() = runTest {
        fakeRecipeRepo.recipesToReturn = emptyList()
        fakeRecipeRepo.shouldFailRefresh = false

        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.isRefreshing)
        assertTrue(state.recipes.isEmpty())
        assertNull(state.errorMessage)
        assertFalse(state.isOfflineWarning)
    }

    @Test
    fun `refreshRecipes clears offline warning when network comes back online`() = runTest {
        fakeRecipeRepo.recipesToReturn = listOf(
            Recipe(id = 2, nombre = "Ceviche de Pescado", ingredientes = "Pescado, Limon", instrucciones = "Marinar")
        )
        fakeRecipeRepo.shouldFailRefresh = true

        val viewModel = createViewModel()
        runCurrent()
        assertTrue(viewModel.uiState.value.isOfflineWarning)

        // La red vuelve a estar disponible
        fakeRecipeRepo.shouldFailRefresh = false
        viewModel.refreshRecipes(isManual = true)
        runCurrent()

        val updatedState = viewModel.uiState.value
        assertFalse(updatedState.isOfflineWarning)
        assertNull(updatedState.errorMessage)
    }

    @Test
    fun `logout triggers clearLocalData on repository to avoid leaking data between sessions`() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.logout()
        runCurrent()

        assertEquals(1, fakeRecipeRepo.clearLocalDataCallCount)
    }
}
