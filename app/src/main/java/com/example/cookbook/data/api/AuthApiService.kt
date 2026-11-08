package com.example.cookbook.data.api

import com.example.cookbook.data.dto.LoginRequestDto
import com.example.cookbook.data.dto.LoginResponseDto
import com.example.cookbook.data.dto.RegisterRequestDto
import com.example.cookbook.data.dto.RegisterResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Endpoints de autenticación de la API REST (FastAPI).
 */
interface AuthApiService {

    @POST("api/auth/registro")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): RegisterResponseDto

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): LoginResponseDto
}
