package com.example.cookbook

import androidx.lifecycle.SavedStateHandle
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRecipeByIdUseCase
import com.example.cookbook.domain.usecase.IsHapticAvailableUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.domain.usecase.StartCookingTimerUseCase
import com.example.cookbook.ui.recipes.CookingModeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
class CookingModeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRecipeRepo: FakeRecipeRepository
    private lateinit var fakeHapticRepo: FakeHapticRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRecipeRepo = FakeRecipeRepository().apply {
            recipesToReturn = listOf(
                Recipe(
                    id = 10,
                    nombre = "Risotto de Hongos",
                    ingredientes = "Arroz arborio, Hongos, Caldo",
                    instrucciones = "Cocinar a fuego lento revolviendo",
                    tiempoPreparacion = 25
                )
            )
        }
        fakeHapticRepo = FakeHapticRepository(isAvailable = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        recipeId: Int = 10,
        hapticAvailable: Boolean = true
    ): CookingModeViewModel {
        fakeHapticRepo.isAvailable = hapticAvailable
        val performHapticUseCase = PerformHapticUseCase(fakeHapticRepo)
        val isHapticAvailableUseCase = IsHapticAvailableUseCase(fakeHapticRepo)
        val getRecipeByIdUseCase = GetRecipeByIdUseCase(fakeRecipeRepo)
        val startCookingTimerUseCase = StartCookingTimerUseCase(performHapticUseCase)

        return CookingModeViewModel(
            savedStateHandle = SavedStateHandle(mapOf("recipeId" to recipeId)),
            getRecipeByIdUseCase = getRecipeByIdUseCase,
            startCookingTimerUseCase = startCookingTimerUseCase,
            isHapticAvailableUseCase = isHapticAvailableUseCase,
            performHapticUseCase = performHapticUseCase
        )
    }

    @Test
    fun initialLoad_loadsRecipeDetails_andPreparationTime() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals("Risotto de Hongos", state.recipeName)
        assertEquals(25, state.initialMinutes)
        assertEquals(25 * 60, state.remainingSeconds)
        assertFalse(state.isRunning)
        assertFalse(state.isFinished)
        assertTrue(state.isVibrationAvailable)
    }

    @Test
    fun incrementAndDecrementMinutes_modifiesInitialAndRemainingTime() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        viewModel.incrementMinutes()
        assertEquals(26, viewModel.uiState.value.initialMinutes)
        assertEquals(26 * 60, viewModel.uiState.value.remainingSeconds)

        viewModel.decrementMinutes()
        assertEquals(25, viewModel.uiState.value.initialMinutes)
        assertEquals(25 * 60, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun startPauseAndReset_updatesViewModelStateCorrectly() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        // Iniciar
        viewModel.startTimer()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRunning)

        // Avanzar 5 segundos en tiempo virtual
        advanceTimeBy(5000L)
        runCurrent()
        assertEquals((25 * 60) - 5, viewModel.uiState.value.remainingSeconds)

        // Pausar
        viewModel.pauseTimer()
        runCurrent()
        assertFalse(viewModel.uiState.value.isRunning)
        val pausedSeconds = viewModel.uiState.value.remainingSeconds
        assertEquals((25 * 60) - 5, pausedSeconds)

        // El tiempo no debe avanzar mientras está pausado
        advanceTimeBy(3000L)
        runCurrent()
        assertEquals(pausedSeconds, viewModel.uiState.value.remainingSeconds)

        // Reiniciar
        viewModel.resetTimer()
        runCurrent()
        assertFalse(viewModel.uiState.value.isRunning)
        assertFalse(viewModel.uiState.value.isFinished)
        assertEquals(25 * 60, viewModel.uiState.value.remainingSeconds)
    }

    @Test
    fun timerFinish_triggersTimerDoneHaptic_andMarksFinished() = runTest {
        val viewModel = createViewModel()
        runCurrent()

        // Reducir minutos a 1 para prueba rápida
        while (viewModel.uiState.value.initialMinutes > 1) {
            viewModel.decrementMinutes()
        }
        assertEquals(1, viewModel.uiState.value.initialMinutes)
        assertEquals(60, viewModel.uiState.value.remainingSeconds)

        fakeHapticRepo.clear()

        viewModel.startTimer()
        runCurrent()
        assertTrue(viewModel.uiState.value.isRunning)

        // Avanzar los 60 segundos
        advanceTimeBy(60_000L)
        runCurrent()

        val state = viewModel.uiState.value
        assertEquals(0, state.remainingSeconds)
        assertFalse(state.isRunning)
        assertTrue(state.isFinished)

        // Verifica que se disparó TIMER_DONE exactamente una vez
        assertEquals(1, fakeHapticRepo.performedEffects.count { it == HapticEffect.TIMER_DONE })
    }

    @Test
    fun whenVibrationUnavailable_uiStateReflectsIt_andDoesNotCrash() = runTest {
        val viewModel = createViewModel(hapticAvailable = false)
        runCurrent()

        assertFalse(viewModel.uiState.value.isVibrationAvailable)
    }
}
