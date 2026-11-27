package com.example.cookbook.di

import com.example.cookbook.data.repository.NetworkAuthRepository
import com.example.cookbook.data.repository.NetworkRecipeRepository
import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        networkAuthRepository: NetworkAuthRepository
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        networkRecipeRepository: NetworkRecipeRepository
    ): RecipeRepository
}
