package com.example.cookbook.data.repository

import com.example.cookbook.data.api.AuthApiService
import com.example.cookbook.data.dto.LoginRequestDto
import com.example.cookbook.data.dto.RegisterRequestDto
import com.example.cookbook.data.local.SessionManager
import com.example.cookbook.data.mapper.toDomain
import com.example.cookbook.domain.model.UserSession
import com.example.cookbook.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException

class NetworkAuthRepository(
    private val authApiService: AuthApiService,
    private val sessionManager: SessionManager
) : AuthRepository {

    override suspend fun register(nombre: String, correo: String, password: String): Result<String> {
        return try {
            val request = RegisterRequestDto(
                nombre = nombre.trim(),
                correo = correo.trim(),
                password = password
            )
            val response = authApiService.register(request)
            val message = response.mensaje ?: response.message ?: "Registro completado con éxito."
            Result.success(message)
        } catch (e: HttpException) {
            val errorMessage = parseHttpError(e)
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión. Verifica el acceso a la red de tu dispositivo."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Ocurrió un error inesperado durante el registro."))
        }
    }

    override suspend fun login(correo: String, password: String): Result<UserSession> {
        return try {
            val request = LoginRequestDto(
                correo = correo.trim(),
                password = password
            )
            val response = authApiService.login(request)
            val session = response.toDomain(email = correo.trim())

            sessionManager.saveSession(
                token = session.token,
                tokenType = session.tokenType,
                email = session.email
            )

            Result.success(session)
        } catch (e: HttpException) {
            val errorMessage = parseHttpError(e)
            Result.failure(Exception(errorMessage))
        } catch (e: IOException) {
            Result.failure(Exception("Error de conexión. Verifica el acceso a la red de tu dispositivo."))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Ocurrió un error inesperado al iniciar sesión."))
        }
    }

    override fun logout() {
        sessionManager.clearSession()
    }

    override fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    override fun getSession(): UserSession? {
        val token = sessionManager.getAuthToken() ?: return null
        return UserSession(
            token = token,
            tokenType = sessionManager.getTokenType(),
            email = sessionManager.getUserEmail(),
            name = sessionManager.getUserName()
        )
    }

    private fun parseHttpError(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank() && errorBody.contains("detail")) {
                val detailPattern = """"detail"\s*:\s*"([^"]+)"""".toRegex()
                val match = detailPattern.find(errorBody)
                if (match != null) {
                    return match.groupValues[1]
                }
            }
            when (e.code()) {
                400 -> "Datos incorrectos. Revisa los campos enviados."
                401 -> "Correo o contraseña incorrectos."
                404 -> "Servicio o usuario no encontrado."
                409 -> "El correo electrónico ya está registrado."
                422 -> "Error de validación. Revisa el formato de correo o contraseña."
                500 -> "Error en el servidor. Intenta de nuevo más tarde."
                else -> "Error en la petición (Código ${e.code()})"
            }
        } catch (_: Exception) {
            "Error de comunicación con el servidor (Código ${e.code()})"
        }
    }
}
