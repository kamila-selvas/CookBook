package com.example.cookbook.data.mapper

import com.example.cookbook.data.dto.LoginResponseDto
import com.example.cookbook.domain.model.UserSession

fun LoginResponseDto.toDomain(email: String? = null): UserSession {
    return UserSession(
        token = accessToken,
        tokenType = tokenType,
        email = email
    )
}
