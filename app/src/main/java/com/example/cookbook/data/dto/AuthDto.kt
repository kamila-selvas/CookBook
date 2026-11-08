package com.example.cookbook.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de registro de usuario.
 * POST /api/auth/registro
 */
data class RegisterRequestDto(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("password")
    val password: String
)

/**
 * DTO para la respuesta del registro.
 */
data class RegisterResponseDto(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("nombre")
    val nombre: String? = null,

    @SerializedName("correo")
    val correo: String? = null,

    @SerializedName("mensaje")
    val mensaje: String? = null,

    @SerializedName("message")
    val message: String? = null
)

/**
 * DTO para la petición de inicio de sesión.
 * POST /api/auth/login
 */
data class LoginRequestDto(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("password")
    val password: String
)

/**
 * DTO para la respuesta exitosa del login con el JWT.
 */
data class LoginResponseDto(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String = "bearer"
)
