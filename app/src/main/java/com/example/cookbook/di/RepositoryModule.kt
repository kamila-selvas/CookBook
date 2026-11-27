package com.example.cookbook.di

import com.example.cookbook.data.repository.HapticRepositoryImpl
import com.example.cookbook.data.repository.NetworkAuthRepository
import com.example.cookbook.data.repository.PhotoRepositoryImpl
import com.example.cookbook.data.repository.RecipeRepositoryImpl
import com.example.cookbook.data.repository.ShakeRepositoryImpl
import com.example.cookbook.domain.repository.AuthRepository
import com.example.cookbook.domain.repository.HapticRepository
import com.example.cookbook.domain.repository.PhotoRepository
import com.example.cookbook.domain.repository.RecipeRepository
import com.example.cookbook.domain.repository.ShakeRepository
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
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository

    @Binds
    @Singleton
    abstract fun bindHapticRepository(
        hapticRepositoryImpl: HapticRepositoryImpl
    ): HapticRepository

    @Binds
    @Singleton
    abstract fun bindShakeRepository(
        shakeRepositoryImpl: ShakeRepositoryImpl
    ): ShakeRepository

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}
