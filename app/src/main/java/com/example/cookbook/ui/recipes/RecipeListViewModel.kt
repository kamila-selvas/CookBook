package com.example.cookbook.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.usecase.GetRandomRecipeUseCase
import com.example.cookbook.domain.usecase.GetRecipesUseCase
import com.example.cookbook.domain.usecase.LogoutUseCase
import com.example.cookbook.domain.usecase.ObserveShakeUseCase
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RecipeListEvent {
    data class NavigateToRecipe(val recipeId: Int) : RecipeListEvent
    data class ShowMessage(val message: String) : RecipeListEvent
}

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null,
    val isShakeAvailable: Boolean = true,
    val randomRecipeMessage: String? = null
)

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val getRecipesUseCase: GetRecipesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getRandomRecipeUseCase: GetRandomRecipeUseCase,
    private val observeShakeUseCase: ObserveShakeUseCase,
    private val performHapticUseCase: PerformHapticUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        RecipeListUiState(
            isLoading = true,
            isShakeAvailable = observeShakeUseCase.isAvailable
        )
    )
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    private val _eventChannel = Channel<RecipeListEvent>(Channel.BUFFERED)
    val events: Flow<RecipeListEvent> = _eventChannel.receiveAsFlow()

    init {
        loadRecipes()
    }

    fun observeShakes(): Flow<Unit> {
        return observeShakeUseCase()
    }

    fun onRandomRecipeRequested() {
        viewModelScope.launch {
            val result = getRandomRecipeUseCase()
            result.fold(
                onSuccess = { recipe ->
                    if (recipe != null) {
                        performHapticUseCase(HapticEffect.SUCCESS)
                        _eventChannel.send(RecipeListEvent.NavigateToRecipe(recipe.id))
                    } else {
                        val msg = "No hay recetas disponibles para descubrir."
                        _uiState.update { it.copy(randomRecipeMessage = msg) }
                        _eventChannel.send(RecipeListEvent.ShowMessage(msg))
                    }
                },
                onFailure = { error ->
                    val msg = error.message ?: "No se pudo obtener una receta aleatoria."
                    _uiState.update { it.copy(randomRecipeMessage = msg) }
                    _eventChannel.send(RecipeListEvent.ShowMessage(msg))
                }
            )
        }
    }

    fun clearRandomRecipeMessage() {
        _uiState.update { it.copy(randomRecipeMessage = null) }
    }

    fun loadRecipes(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = getRecipesUseCase()
            result.fold(
                onSuccess = { recipeList ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            isRefreshing = false,
                            recipes = recipeList,
                            filteredRecipes = filterList(recipeList, current.searchQuery, current.selectedCategory),
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = error.message ?: "Error al cargar las recetas."
                        )
                    }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredRecipes = filterList(current.recipes, query, current.selectedCategory)
            )
        }
    }

    fun onCategorySelect(category: String?) {
        _uiState.update { current ->
            val newCategory = if (current.selectedCategory == category) null else category
            current.copy(
                selectedCategory = newCategory,
                filteredRecipes = filterList(current.recipes, current.searchQuery, newCategory)
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    private fun filterList(
        list: List<Recipe>,
        query: String,
        category: String?
    ): List<Recipe> {
        return list.filter { recipe ->
            val matchesQuery = query.isBlank() ||
                    recipe.nombre.contains(query, ignoreCase = true) ||
                    (recipe.descripcion?.contains(query, ignoreCase = true) == true) ||
                    recipe.ingredientes.contains(query, ignoreCase = true)

            val matchesCategory = category == null ||
                    recipe.categoria?.equals(category, ignoreCase = true) == true

            matchesQuery && matchesCategory
        }
    }
}
