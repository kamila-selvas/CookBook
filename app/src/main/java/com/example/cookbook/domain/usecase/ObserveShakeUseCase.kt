package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.ShakeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveShakeUseCase @Inject constructor(
    private val shakeRepository: ShakeRepository
) {
    val isAvailable: Boolean
        get() = shakeRepository.isAvailable

    operator fun invoke(): Flow<Unit> {
        return shakeRepository.shakes()
    }
}
