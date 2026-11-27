package com.example.cookbook.di

import com.example.cookbook.data.hardware.CameraXDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CameraEntryPoint {
    fun cameraXDataSource(): CameraXDataSource
}
