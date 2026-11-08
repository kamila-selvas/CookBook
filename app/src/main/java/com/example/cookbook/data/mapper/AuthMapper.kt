package com.example.cookbook.data.mapper

import com.example.cookbook.data.dto.LoginResponseDto
import com.example.cookbook.domain.model.UserSession

/**
 * Mappers para transformar respuestas DTO de autenticación a modelos de Dominio.
 */
fun LoginResponseDto.toDomain(email: String? = null): UserSession {
    return UserSession(
        token = accessToken,
        tokenType = tokenType,
        email = email
    )
}
