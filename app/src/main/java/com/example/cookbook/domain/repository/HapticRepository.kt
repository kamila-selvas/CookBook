package com.example.cookbook.domain.repository

import com.example.cookbook.domain.model.HapticEffect

interface HapticRepository {
    val isAvailable: Boolean
    fun performHaptic(effect: HapticEffect)
}
