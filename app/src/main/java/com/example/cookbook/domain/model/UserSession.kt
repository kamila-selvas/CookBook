package com.example.cookbook.domain.model

data class UserSession(
    val token: String,
    val tokenType: String = "bearer",
    val email: String? = null,
    val name: String? = null
)
