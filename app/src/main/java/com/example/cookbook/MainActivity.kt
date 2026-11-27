package com.example.cookbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cookbook.domain.usecase.IsLoggedInUseCase
import com.example.cookbook.ui.navigation.AppNavHost
import com.example.cookbook.ui.navigation.Screen
import com.example.cookbook.ui.theme.CookBookTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var isLoggedInUseCase: IsLoggedInUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startDestination = if (isLoggedInUseCase()) {
            Screen.RecipeList.route
        } else {
            Screen.Login.route
        }

        setContent {
            CookBookTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(startDestination = startDestination)
                }
            }
        }
    }
}
