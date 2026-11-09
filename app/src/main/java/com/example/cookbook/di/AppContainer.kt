package com.example.cookbook.di

import android.content.Context
import com.example.cookbook.data.api.AuthApiService
import com.example.cookbook.data.api.AuthInterceptor
import com.example.cookbook.data.api.RecipeApiService
import com.example.cookbook.data.local.SessionManager
import com.example.cookbook.data.repository.NetworkAuthRepository
import com.example.cookbook.data.repository.NetworkRecipeRepository
import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.repository.RecipeRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

interface AppContainer {
    val sessionManager: SessionManager
    val authApiService: AuthApiService
    val recipeApiService: RecipeApiService
    val authRepository: AuthRepository
    val recipeRepository: RecipeRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val baseUrl = "http://54.146.216.114:8000/"

    override val sessionManager: SessionManager by lazy {
        SessionManager(context)
    }

    private val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(sessionManager)
    }

    private val loggingInterceptor: HttpLoggingInterceptor by lazy {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
            redactHeader("Authorization")
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    override val recipeApiService: RecipeApiService by lazy {
        retrofit.create(RecipeApiService::class.java)
    }

    override val authRepository: AuthRepository by lazy {
        NetworkAuthRepository(authApiService, sessionManager)
    }

    override val recipeRepository: RecipeRepository by lazy {
        NetworkRecipeRepository(recipeApiService)
    }
}
