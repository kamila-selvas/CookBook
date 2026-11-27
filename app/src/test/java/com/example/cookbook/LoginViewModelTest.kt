package com.example.cookbook

import com.example.cookbook.domain.model.UserSession
import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.usecase.LoginUseCase
import com.example.cookbook.ui.auth.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FakeAuthRepository : AuthRepository {
    override suspend fun register(nombre: String, correo: String, password: String): Result<String> =
        Result.success("Registro exitoso")

    override suspend fun login(correo: String, password: String): Result<UserSession> =
        Result.success(UserSession(token = "fake_token", email = correo))

    override fun logout() {}
    override fun isLoggedIn(): Boolean = true
    override fun getSession(): UserSession? = UserSession(token = "fake_token", email = "user@test.com")
}

class LoginViewModelTest {

    @Test
    fun loginViewModel_updatesState_andValidatesEmptyFields() {
        val fakeRepo = FakeAuthRepository()
        val loginUseCase = LoginUseCase(fakeRepo)
        val viewModel = LoginViewModel(loginUseCase)

        assertEquals("", viewModel.uiState.value.correo)
        assertEquals("", viewModel.uiState.value.password)

        viewModel.onCorreoChange("chef@cookbook.com")
        viewModel.onPasswordChange("password123")

        assertEquals("chef@cookbook.com", viewModel.uiState.value.correo)
        assertEquals("password123", viewModel.uiState.value.password)

        // Validar validación con correo vacío
        viewModel.onCorreoChange("")
        viewModel.login()
        assertEquals("Por favor ingresa tu correo electrónico.", viewModel.uiState.value.errorMessage)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
