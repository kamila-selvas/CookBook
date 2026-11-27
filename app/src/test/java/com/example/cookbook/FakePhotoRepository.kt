package com.example.cookbook

import com.example.cookbook.domain.repository.PhotoRepository

class FakePhotoRepository(
    override var isCameraAvailable: Boolean = true
) : PhotoRepository {

    var shouldFail: Boolean = false
    var failureException: Exception = Exception("Error simulado de captura")
    var nextPhotoPath: String = "/data/user/0/com.example.cookbook/files/photos/fake_recipe_photo.jpg"
    var takePhotoCallCount: Int = 0

    override suspend fun takePhoto(): Result<String> {
        takePhotoCallCount++
        if (!isCameraAvailable) {
            return Result.failure(IllegalStateException("No hay hardware de cámara disponible"))
        }
        if (shouldFail) {
            return Result.failure(failureException)
        }
        return Result.success(nextPhotoPath)
    }
}
