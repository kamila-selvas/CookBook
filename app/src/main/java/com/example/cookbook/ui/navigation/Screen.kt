package com.example.cookbook.ui.navigation

/**
 * Rutas tipadas para la navegación dentro de CookBook.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object RecipeList : Screen("recipe_list")
    data object RecipeDetail : Screen("recipe_detail/{recipeId}") {
        fun createRoute(recipeId: Int): String = "recipe_detail/$recipeId"
    }
    data object RecipeCreate : Screen("recipe_create")
    data object RecipeEdit : Screen("recipe_edit/{recipeId}") {
        fun createRoute(recipeId: Int): String = "recipe_edit/$recipeId"
    }
}
