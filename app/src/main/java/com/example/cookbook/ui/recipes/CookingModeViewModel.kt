package com.example.cookbook.ui.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.usecase.GetRecipeByIdUseCase
import com.example.cookbook.domain.usecase.IsHapticAvailableUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.domain.usecase.StartCookingTimerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CookingModeUiState(
    val recipeName: String = "",
    val initialMinutes: Int = 15,
    val remainingSeconds: Int = 15 * 60,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val isVibrationAvailable: Boolean = true,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class CookingModeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val startCookingTimerUseCase: StartCookingTimerUseCase,
    private val isHapticAvailableUseCase: IsHapticAvailableUseCase,
    private val performHapticUseCase: PerformHapticUseCase
) : ViewModel() {

    private val recipeId: Int = savedStateHandle.get<Int>("recipeId") ?: 0
    private var timerJob: Job? = null

    private val _uiState = MutableStateFlow(
        CookingModeUiState(
            isVibrationAvailable = isHapticAvailableUseCase()
        )
    )
    val uiState: StateFlow<CookingModeUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getRecipeByIdUseCase(recipeId)
            result.fold(
                onSuccess = { recipe ->
                    val defaultMinutes = (recipe.tiempoPreparacion?.takeIf { it > 0 } ?: 15)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipeName = recipe.nombre,
                            initialMinutes = defaultMinutes,
                            remainingSeconds = defaultMinutes * 60,
                            isRunning = false,
                            isFinished = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "No se pudo cargar la receta."
                        )
                    }
                }
            )
        }
    }

    fun incrementMinutes() {
        if (_uiState.value.isRunning) return
        _uiState.update { current ->
            val newMin = (current.initialMinutes + 1).coerceAtMost(180)
            current.copy(
                initialMinutes = newMin,
                remainingSeconds = newMin * 60,
                isFinished = false
            )
        }
        performHapticUseCase(HapticEffect.LIGHT_CLICK)
    }

    fun decrementMinutes() {
        if (_uiState.value.isRunning) return
        _uiState.update { current ->
            val newMin = (current.initialMinutes - 1).coerceAtLeast(1)
            current.copy(
                initialMinutes = newMin,
                remainingSeconds = newMin * 60,
                isFinished = false
            )
        }
        performHapticUseCase(HapticEffect.LIGHT_CLICK)
    }

    fun startTimer() {
        if (_uiState.value.isRunning) return
        val seconds = if (_uiState.value.isFinished || _uiState.value.remainingSeconds <= 0) {
            _uiState.value.initialMinutes * 60
        } else {
            _uiState.value.remainingSeconds
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            _uiState.update { it.copy(isRunning = true, isFinished = false, remainingSeconds = seconds) }
            startCookingTimerUseCase.fromSeconds(seconds).collect { sec ->
                _uiState.update { current ->
                    current.copy(
                        remainingSeconds = sec,
                        isRunning = sec > 0,
                        isFinished = sec == 0
                    )
                }
            }
        }
        performHapticUseCase(HapticEffect.LIGHT_CLICK)
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { it.copy(isRunning = false) }
        performHapticUseCase(HapticEffect.LIGHT_CLICK)
    }

    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.update { current ->
            current.copy(
                isRunning = false,
                isFinished = false,
                remainingSeconds = current.initialMinutes * 60
            )
        }
        performHapticUseCase(HapticEffect.LIGHT_CLICK)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        timerJob = null
    }
}
