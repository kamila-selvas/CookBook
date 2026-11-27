package com.example.cookbook

import com.example.cookbook.domain.repository.ShakeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeShakeRepository(
    override var isAvailable: Boolean = true
) : ShakeRepository {

    private val _shakesFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun shakes(): Flow<Unit> = _shakesFlow.asSharedFlow()

    suspend fun emitShake() {
        _shakesFlow.emit(Unit)
    }

    fun tryEmitShake(): Boolean {
        return _shakesFlow.tryEmit(Unit)
    }
}
