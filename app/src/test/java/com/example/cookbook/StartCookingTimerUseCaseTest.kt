package com.example.cookbook

import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.usecase.PerformHapticUseCase
import com.example.cookbook.domain.usecase.StartCookingTimerUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StartCookingTimerUseCaseTest {

    @Test
    fun timer_countsDown_and_triggers_timerDone_once_at_zero() = runTest {
        val fakeHapticRepo = FakeHapticRepository()
        val performHapticUseCase = PerformHapticUseCase(fakeHapticRepo)
        val useCase = StartCookingTimerUseCase(performHapticUseCase)

        val secondsEmitted = mutableListOf<Int>()
        val job = launch {
            useCase.fromSeconds(3).collect { sec ->
                secondsEmitted.add(sec)
            }
        }

        runCurrent()
        assertEquals(listOf(3), secondsEmitted)
        assertEquals(0, fakeHapticRepo.performedEffects.size)

        advanceTimeBy(1000L)
        runCurrent()
        assertEquals(listOf(3, 2), secondsEmitted)

        advanceTimeBy(1000L)
        runCurrent()
        assertEquals(listOf(3, 2, 1), secondsEmitted)

        advanceTimeBy(1000L)
        runCurrent()
        assertEquals(listOf(3, 2, 1, 0), secondsEmitted)

        job.cancel()

        // Verifica que TIMER_DONE se disparó exactamente una vez
        assertEquals(1, fakeHapticRepo.performedEffects.count { it == HapticEffect.TIMER_DONE })
    }

    @Test
    fun timer_cancelledEarly_doesNotTrigger_timerDone() = runTest {
        val fakeHapticRepo = FakeHapticRepository()
        val performHapticUseCase = PerformHapticUseCase(fakeHapticRepo)
        val useCase = StartCookingTimerUseCase(performHapticUseCase)

        val secondsEmitted = mutableListOf<Int>()
        val job = launch {
            useCase.fromSeconds(5).collect { sec ->
                secondsEmitted.add(sec)
            }
        }

        advanceTimeBy(2000L) // llega a 3
        runCurrent()
        job.cancel()

        advanceTimeBy(5000L)
        runCurrent()

        assertEquals(0, fakeHapticRepo.performedEffects.count { it == HapticEffect.TIMER_DONE })
    }
}
