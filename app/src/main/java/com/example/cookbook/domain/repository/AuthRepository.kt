package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.UserSession

/**
 * Contrato de repositorio para operaciones de autenticación y sesión.
 */
interface AuthRepository {
    /**
     * Registra un nuevo usuario en la API.
     */
    suspend fun register(nombre: String, correo: String, password: String): Result<String>

    /**
     * Inicia sesión con correo y contraseña, guardando el token internamente.
     */
    suspend fun login(correo: String, password: String): Result<UserSession>

    /**
     * Cierra la sesión activa y elimina el token de almacenamiento seguro.
     */
    fun logout()

    /**
     * Verifica si existe una sesión activa y válida.
     */
    fun isLoggedIn(): Boolean

    /**
     * Obtiene los datos de la sesión actual si existe.
     */
    fun getSession(): UserSession?
}
