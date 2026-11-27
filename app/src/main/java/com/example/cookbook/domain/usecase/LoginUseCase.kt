package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.UserSession
import com.example.cookbook.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(correo: String, password: String): Result<UserSession> {
        return authRepository.login(correo, password)
    }
}
