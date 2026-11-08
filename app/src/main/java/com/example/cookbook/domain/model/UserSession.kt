package com.example.cookbook.domain.model

/**
 * Modelo de dominio que representa la sesión activa de un usuario.
 */
data class UserSession(
    val token: String,
    val tokenType: String = "bearer",
    val email: String? = null,
    val name: String? = null
)
