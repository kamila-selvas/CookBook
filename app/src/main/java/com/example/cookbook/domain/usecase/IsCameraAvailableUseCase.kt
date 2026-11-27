package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.PhotoRepository
import javax.inject.Inject

class IsCameraAvailableUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    operator fun invoke(): Boolean {
        return photoRepository.isCameraAvailable
    }
}
