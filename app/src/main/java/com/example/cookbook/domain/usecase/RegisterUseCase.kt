package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(nombre: String, correo: String, password: String): Result<String> {
        return authRepository.register(nombre, correo, password)
    }
}
