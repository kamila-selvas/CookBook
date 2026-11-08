package com.example.cookbook.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookbook.CookBookApplication
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val errorMessage: String? = null
)

class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState(isLoading = true))
    val uiState: StateFlow<RecipeListUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }

            val result = recipeRepository.getRecipes()
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
        authRepository.logout()
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

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CookBookApplication)
                RecipeListViewModel(
                    recipeRepository = application.container.recipeRepository,
                    authRepository = application.container.authRepository
                )
            }
        }
    }
}
