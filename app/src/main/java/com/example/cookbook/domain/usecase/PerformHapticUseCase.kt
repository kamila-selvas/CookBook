package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.repository.HapticRepository
import javax.inject.Inject

class PerformHapticUseCase @Inject constructor(
    private val hapticRepository: HapticRepository
) {
    operator fun invoke(effect: HapticEffect) {
        hapticRepository.performHaptic(effect)
    }
}
