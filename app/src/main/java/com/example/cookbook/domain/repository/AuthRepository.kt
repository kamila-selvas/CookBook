package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.UserSession

interface AuthRepository {
    suspend fun register(nombre: String, correo: String, password: String): Result<String>

    suspend fun login(correo: String, password: String): Result<UserSession>

    fun logout()

    fun isLoggedIn(): Boolean

    fun getSession(): UserSession?
}
