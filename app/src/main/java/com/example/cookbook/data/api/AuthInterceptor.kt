package com.example.cookbook.data.api

import com.example.cookbook.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = sessionManager.getAuthToken()
        if (token.isNullOrBlank() || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val tokenType = sessionManager.getTokenType().replaceFirstChar { it.uppercase() }
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "$tokenType $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
