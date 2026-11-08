package com.example.cookbook.ui.navigation

/**
 * Rutas tipadas para la navegación dentro de CookBook.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object Home : Screen("home")
}
