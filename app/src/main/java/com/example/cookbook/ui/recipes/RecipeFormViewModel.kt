package com.example.cookbook.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeFormUiState(
    val id: Int? = null,
    val nombre: String = "",
    val descripcion: String = "",
    val ingredientes: String = "",
    val instrucciones: String = "",
    val categoria: String = "",
    val imagenUrl: String = "",
    val tiempoPreparacion: String = "",
    val porciones: String = "",
    val esFavorita: Boolean = false,
    val isLoadingRecipe: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class RecipeFormViewModel(
    private val recipeId: Int?,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeFormUiState(id = recipeId))
    val uiState: StateFlow<RecipeFormUiState> = _uiState.asStateFlow()

    init {
        if (recipeId != null && recipeId > 0) {
            loadExistingRecipe(recipeId)
        }
    }

    private fun loadExistingRecipe(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingRecipe = true, errorMessage = null) }
            val result = recipeRepository.getRecipeById(id)
            result.fold(
                onSuccess = { recipe ->
                    _uiState.update { current ->
                        current.copy(
                            isLoadingRecipe = false,
                            nombre = recipe.nombre,
                            descripcion = recipe.descripcion ?: "",
                            ingredientes = recipe.ingredientes,
                            instrucciones = recipe.instrucciones,
                            categoria = recipe.categoria ?: "",
                            imagenUrl = recipe.imagenUrl ?: "",
                            tiempoPreparacion = recipe.tiempoPreparacion?.toString() ?: "",
                            porciones = recipe.porciones?.toString() ?: "",
                            esFavorita = recipe.esFavorita,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoadingRecipe = false,
                            errorMessage = error.message ?: "No se pudo cargar la receta para editar."
                        )
                    }
                }
            )
        }
    }

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombre = value, errorMessage = null) }
    }

    fun onDescripcionChange(value: String) {
        _uiState.update { it.copy(descripcion = value, errorMessage = null) }
    }

    fun onIngredientesChange(value: String) {
        _uiState.update { it.copy(ingredientes = value, errorMessage = null) }
    }

    fun onInstruccionesChange(value: String) {
        _uiState.update { it.copy(instrucciones = value, errorMessage = null) }
    }

    fun onCategoriaChange(value: String) {
        _uiState.update { it.copy(categoria = value, errorMessage = null) }
    }

    fun onImagenUrlChange(value: String) {
        _uiState.update { it.copy(imagenUrl = value, errorMessage = null) }
    }

    fun onTiempoChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.update { it.copy(tiempoPreparacion = value, errorMessage = null) }
        }
    }

    fun onPorcionesChange(value: String) {
        if (value.isEmpty() || value.all { it.isDigit() }) {
            _uiState.update { it.copy(porciones = value, errorMessage = null) }
        }
    }

    fun onFavoritaChange(value: Boolean) {
        _uiState.update { it.copy(esFavorita = value) }
    }

    fun saveRecipe() {
        val state = _uiState.value
        val nombre = state.nombre.trim()
        val ingredientes = state.ingredientes.trim()
        val instrucciones = state.instrucciones.trim()

        // Validaciones obligatorias
        if (nombre.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre de la receta es obligatorio.") }
            return
        }
        if (ingredientes.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Debes ingresar al menos un ingrediente.") }
            return
        }
        if (instrucciones.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Debes ingresar las instrucciones de preparación.") }
            return
        }

        val tiempo = state.tiempoPreparacion.toIntOrNull()
        val porciones = state.porciones.toIntOrNull()

        val recipeToSave = Recipe(
            id = recipeId ?: 0,
            nombre = nombre,
            descripcion = state.descripcion.trim().ifBlank { null },
            ingredientes = ingredientes,
            instrucciones = instrucciones,
            categoria = state.categoria.trim().ifBlank { null },
            imagenUrl = state.imagenUrl.trim().ifBlank { null },
            tiempoPreparacion = tiempo,
            porciones = porciones,
            esFavorita = state.esFavorita
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = if (recipeId != null && recipeId > 0) {
                recipeRepository.updateRecipe(recipeId, recipeToSave)
            } else {
                recipeRepository.createRecipe(recipeToSave)
            }

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSaving = false, isSuccess = true) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "No se pudo guardar la receta."
                        )
                    }
                }
            )
        }
    }

    companion object {
        fun provideFactory(
            recipeId: Int?,
            recipeRepository: RecipeRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RecipeFormViewModel(recipeId, recipeRepository) as T
            }
        }
    }
}
