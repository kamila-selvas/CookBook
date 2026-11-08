package com.example.cookbook.data.api

import com.example.cookbook.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor de OkHttp que intercepta las peticiones salientes
 * y adjunta automáticamente la cabecera 'Authorization: Bearer TOKEN'
 * cuando existe un token almacenado en SessionManager.
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si la petición ya tiene cabecera Authorization (o no hay sesión), continúa
        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank() || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Construye la cabecera con el formato "Bearer <token>"
        val tokenType = sessionManager.getTokenType().replaceFirstChar { it.uppercase() }
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "$tokenType $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
