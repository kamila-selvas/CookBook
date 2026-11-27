package com.example.cookbook.domain.model

sealed interface ResourceResult<out T> {
    data class Success<out T>(val data: T) : ResourceResult<T>
    data class NetworkError(val message: String = "Sin conexión a internet. Mostrando datos guardados.") : ResourceResult<Nothing>
    data class ServerError(val code: Int, val message: String) : ResourceResult<Nothing>
    data object Unauthorized : ResourceResult<Nothing>
    data class UnknownError(val message: String) : ResourceResult<Nothing>
}
