package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.HapticRepository
import javax.inject.Inject

class IsHapticAvailableUseCase @Inject constructor(
    private val hapticRepository: HapticRepository
) {
    operator fun invoke(): Boolean {
        return hapticRepository.isAvailable
    }
}
