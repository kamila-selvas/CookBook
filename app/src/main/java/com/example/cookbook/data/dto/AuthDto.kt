package com.example.cookbook.data.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("correo")
    val correo: String,

    @SerializedName("password")
    val password: String
)

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

data class LoginRequestDto(
    @SerializedName("correo")
    val correo: String,

    @SerializedName("password")
    val password: String
)

data class LoginResponseDto(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String = "bearer"
)
