package com.example.cookbook

import com.example.cookbook.domain.model.HapticEffect
import com.example.cookbook.domain.repository.HapticRepository

class FakeHapticRepository(
    override var isAvailable: Boolean = true
) : HapticRepository {
    val performedEffects = mutableListOf<HapticEffect>()

    override fun performHaptic(effect: HapticEffect) {
        if (isAvailable) {
            performedEffects.add(effect)
        }
    }

    fun clear() {
        performedEffects.clear()
    }
}
