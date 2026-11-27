package com.example.cookbook.data.repository

import com.example.cookbook.data.hardware.AccelerometerShakeDataSource
import com.example.cookbook.domain.repository.ShakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShakeRepositoryImpl @Inject constructor(
    private val dataSource: AccelerometerShakeDataSource
) : ShakeRepository {

    override val isAvailable: Boolean
        get() = dataSource.isAvailable

    override fun shakes(): Flow<Unit> {
        return dataSource.shakes()
    }
}
