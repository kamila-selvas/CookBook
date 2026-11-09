package com.example.cookbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cookbook.di.AppContainer
import com.example.cookbook.ui.auth.LoginScreen
import com.example.cookbook.ui.auth.LoginViewModel
import com.example.cookbook.ui.auth.RegisterScreen
import com.example.cookbook.ui.auth.RegisterViewModel
import com.example.cookbook.ui.recipes.RecipeDetailScreen
import com.example.cookbook.ui.recipes.RecipeDetailViewModel
import com.example.cookbook.ui.recipes.RecipeFormScreen
import com.example.cookbook.ui.recipes.RecipeFormViewModel
import com.example.cookbook.ui.recipes.RecipeListScreen
import com.example.cookbook.ui.recipes.RecipeListViewModel

@Composable
fun AppNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val startDestination = if (container.authRepository.isLoggedIn()) {
        Screen.RecipeList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.RecipeList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBackToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RecipeList.route) {
            val recipeListViewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)
            RecipeListScreen(
                viewModel = recipeListViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                },
                onCreateRecipeClick = {
                    navController.navigate(Screen.RecipeCreate.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            val detailViewModel: RecipeDetailViewModel = viewModel(
                key = "detail_$recipeId",
                factory = RecipeDetailViewModel.provideFactory(
                    recipeId = recipeId,
                    recipeRepository = container.recipeRepository
                )
            )

            RecipeDetailScreen(
                viewModel = detailViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditRecipe = { id ->
                    navController.navigate(Screen.RecipeEdit.createRoute(id))
                },
                onRecipeDeleted = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.RecipeCreate.route) {
            val formViewModel: RecipeFormViewModel = viewModel(
                factory = RecipeFormViewModel.provideFactory(
                    recipeId = null,
                    recipeRepository = container.recipeRepository
                )
            )
            RecipeFormScreen(
                viewModel = formViewModel,
                isEditMode = false,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.RecipeEdit.route,
            arguments = listOf(
                navArgument("recipeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId") ?: 0
            val formViewModel: RecipeFormViewModel = viewModel(
                key = "edit_$recipeId",
                factory = RecipeFormViewModel.provideFactory(
                    recipeId = recipeId,
                    recipeRepository = container.recipeRepository
                )
            )
            RecipeFormScreen(
                viewModel = formViewModel,
                isEditMode = true,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }
    }
}
