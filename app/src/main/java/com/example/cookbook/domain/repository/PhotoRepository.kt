package com.example.cookbook.domain.repository

interface PhotoRepository {
    val isCameraAvailable: Boolean
    suspend fun takePhoto(): Result<String>
}
