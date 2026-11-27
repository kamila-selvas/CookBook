package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.HapticEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StartCookingTimerUseCase @Inject constructor(
    private val performHapticUseCase: PerformHapticUseCase
) {
    operator fun invoke(minutos: Int): Flow<Int> {
        return fromSeconds(minutos * 60)
    }

    fun fromSeconds(totalSeconds: Int): Flow<Int> = flow {
        var current = totalSeconds
        emit(current)
        while (current > 0) {
            delay(1000L)
            current--
            emit(current)
            if (current == 0) {
                performHapticUseCase(HapticEffect.TIMER_DONE)
            }
        }
    }
}
