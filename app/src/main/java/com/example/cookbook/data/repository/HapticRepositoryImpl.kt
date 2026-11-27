package com.example.cookbook.data.repository

import com.example.cookbook.data.hardware.VibratorDataSource
import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.repository.HapticRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticRepositoryImpl @Inject constructor(
    private val vibratorDataSource: VibratorDataSource
) : HapticRepository {

    override val isAvailable: Boolean
        get() = vibratorDataSource.hasVibrator

    override fun performHaptic(effect: HapticEffect) {
        vibratorDataSource.vibrate(effect)
    }
}
