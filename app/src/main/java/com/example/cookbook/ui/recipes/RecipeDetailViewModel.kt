package com.example.cookbook.ui.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.DeleteRecipeUseCase
import com.example.cookbook.domain.usecase.GetRecipeByIdUseCase
import com.example.cookbook.domain.usecase.MarkFavoriteRecipeUseCase
import com.example.cookbook.domain.usecase.ObserveRecipeByIdUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipe: Recipe? = null,
    val errorMessage: String? = null,
    val isOfflineWarning: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val deleteErrorMessage: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeRecipeByIdUseCase: ObserveRecipeByIdUseCase,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase,
    private val markFavoriteRecipeUseCase: MarkFavoriteRecipeUseCase,
    private val performHapticUseCase: PerformHapticUseCase
) : ViewModel() {

    private val recipeId: Int = savedStateHandle.get<Int>("recipeId") ?: 0

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        observeCachedRecipe()
        loadRecipe()
    }

    private fun observeCachedRecipe() {
        viewModelScope.launch {
            observeRecipeByIdUseCase(recipeId).collect { cached ->
                if (cached != null) {
                    _uiState.update { current ->
                        current.copy(
                            recipe = cached,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun loadRecipe() {
        viewModelScope.launch {
            val hasLocal = _uiState.value.recipe != null
            if (!hasLocal) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null, isOfflineWarning = false) }
            }

            val result = getRecipeByIdUseCase(recipeId)
            result.fold(
                onSuccess = { recipe ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recipe = recipe,
                            errorMessage = null,
                            isOfflineWarning = false
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { current ->
                        if (current.recipe != null) {
                            current.copy(
                                isLoading = false,
                                isOfflineWarning = true
                            )
                        } else {
                            current.copy(
                                isLoading = false,
                                errorMessage = error.message ?: "No se pudo cargar la receta."
                            )
                        }
                    }
                }
            )
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value.recipe ?: return
        viewModelScope.launch {
            val newStatus = !current.esFavorita
            val result = markFavoriteRecipeUseCase(current, newStatus)
            result.fold(
                onSuccess = { updatedRecipe ->
                    _uiState.update { it.copy(recipe = updatedRecipe) }
                    performHapticUseCase(HapticEffect.LIGHT_CLICK)
                },
                onFailure = {
                    // Si falla, el estado local no cambia
                }
            )
        }
    }

    fun deleteRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, deleteErrorMessage = null) }
            val result = deleteRecipeUseCase(recipeId)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            deleteErrorMessage = error.message ?: "No se pudo eliminar la receta."
                        )
                    }
                }
            )
        }
    }
}
