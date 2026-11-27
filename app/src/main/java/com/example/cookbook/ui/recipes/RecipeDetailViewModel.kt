package com.example.cookbook.ui.recipes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.DeleteRecipeUseCase
import com.example.cookbook.domain.usecase.GetRecipeByIdUseCase
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
    val isDeleting: Boolean = false,
    val isDeleted: Boolean = false,
    val deleteErrorMessage: String? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecipeByIdUseCase: GetRecipeByIdUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val recipeId: Int = savedStateHandle.get<Int>("recipeId") ?: 0

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    fun loadRecipe() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = getRecipeByIdUseCase(recipeId)
            result.fold(
                onSuccess = { recipe ->
                    _uiState.update { it.copy(isLoading = false, recipe = recipe, errorMessage = null) }
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
