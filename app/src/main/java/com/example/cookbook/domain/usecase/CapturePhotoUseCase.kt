package com.example.cookbook.domain.usecase

import com.example.cookbook.domain.repository.PhotoRepository
import javax.inject.Inject

class CapturePhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(): Result<String> {
        return photoRepository.takePhoto()
    }
}
