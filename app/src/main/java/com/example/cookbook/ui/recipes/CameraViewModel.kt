package com.example.cookbook.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookbook.domain.usecase.CapturePhotoUseCase
import com.example.cookbook.domain.usecase.IsCameraAvailableUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CameraUiState(
    val hasCameraPermission: Boolean = false,
    val isCameraAvailable: Boolean = true,
    val isCapturing: Boolean = false,
    val capturedPhotoPath: String? = null,
    val errorMessage: String? = null,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val capturePhotoUseCase: CapturePhotoUseCase,
    private val isCameraAvailableUseCase: IsCameraAvailableUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        CameraUiState(
            isCameraAvailable = isCameraAvailableUseCase()
        )
    )
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onPermissionResult(isGranted: Boolean, canRequestAgain: Boolean) {
        _uiState.update { current ->
            if (isGranted) {
                current.copy(
                    hasCameraPermission = true,
                    shouldShowRationale = false,
                    isPermanentlyDenied = false,
                    errorMessage = null
                )
            } else {
                current.copy(
                    hasCameraPermission = false,
                    shouldShowRationale = canRequestAgain,
                    isPermanentlyDenied = !canRequestAgain,
                    errorMessage = if (!canRequestAgain) {
                        "El permiso de cámara fue denegado de forma permanente. Puedes habilitarlo en los Ajustes del sistema."
                    } else {
                        "Se requiere el permiso de cámara para tomar fotos de las recetas."
                    }
                )
            }
        }
    }

    fun takePhoto() {
        if (!_uiState.value.isCameraAvailable) {
            _uiState.update { it.copy(errorMessage = "El dispositivo no cuenta con hardware de cámara.") }
            return
        }
        if (!_uiState.value.hasCameraPermission) {
            _uiState.update { it.copy(errorMessage = "Permiso de cámara no concedido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCapturing = true, errorMessage = null) }
            val result = capturePhotoUseCase()
            result.fold(
                onSuccess = { path ->
                    _uiState.update {
                        it.copy(
                            isCapturing = false,
                            capturedPhotoPath = path,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isCapturing = false,
                            errorMessage = error.message ?: "No se pudo capturar la foto."
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onPhotoHandled() {
        _uiState.update { it.copy(capturedPhotoPath = null) }
    }
}
