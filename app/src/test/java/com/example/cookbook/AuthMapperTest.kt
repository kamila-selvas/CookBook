package com.example.cookbook

import com.example.cookbook.data.dto.LoginResponseDto
import com.example.cookbook.data.mapper.toDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthMapperTest {

    @Test
    fun loginResponseDto_toDomain_mapsTokenAndEmailCorrectly() {
        val dto = LoginResponseDto(
            accessToken = "mock_jwt_token_12345",
            tokenType = "bearer"
        )

        val userSession = dto.toDomain(email = "test@cookbook.com")

        assertEquals("mock_jwt_token_12345", userSession.token)
        assertEquals("bearer", userSession.tokenType)
        assertEquals("test@cookbook.com", userSession.email)
    }
}
