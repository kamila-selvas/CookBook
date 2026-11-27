package com.example.cookbook.domain.repository

import kotlinx.coroutines.flow.Flow

interface ShakeRepository {
    val isAvailable: Boolean
    fun shakes(): Flow<Unit>
}
