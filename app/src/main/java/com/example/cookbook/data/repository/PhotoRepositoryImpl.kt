package com.example.cookbook.data.repository

import com.example.cookbook.data.hardware.CameraXDataSource
import com.example.cookbook.domain.repository.PhotoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val cameraXDataSource: CameraXDataSource
) : PhotoRepository {

    override val isCameraAvailable: Boolean
        get() = cameraXDataSource.isCameraAvailable

    override suspend fun takePhoto(): Result<String> {
        return cameraXDataSource.takePhoto()
    }
}
