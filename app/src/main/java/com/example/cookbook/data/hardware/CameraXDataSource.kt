package com.example.cookbook.data.hardware

import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class CameraXDataSource @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext

    val isCameraAvailable: Boolean
        get() = appContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null

    fun bindCamera(
        lifecycleOwner: LifecycleOwner,
        surfaceProvider: Preview.SurfaceProvider,
        onError: (Throwable) -> Unit = {}
    ) {
        if (!isCameraAvailable) {
            onError(IllegalStateException("No hay hardware de cámara disponible en el dispositivo."))
            return
        }

        try {
            val providerFuture = ProcessCameraProvider.getInstance(appContext)
            providerFuture.addListener({
                try {
                    val provider = providerFuture.get()
                    cameraProvider = provider
                    provider.unbindAll()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(surfaceProvider)
                    }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()
                    imageCapture = capture

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        capture
                    )
                } catch (e: Exception) {
                    onError(e)
                }
            }, ContextCompat.getMainExecutor(appContext))
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun unbindCamera() {
        try {
            cameraProvider?.unbindAll()
        } catch (_: Exception) {
        } finally {
            cameraProvider = null
            imageCapture = null
        }
    }

    suspend fun takePhoto(): Result<String> = suspendCancellableCoroutine { continuation ->
        if (!isCameraAvailable) {
            continuation.resume(Result.failure(IllegalStateException("No hay hardware de cámara disponible en el dispositivo.")))
            return@suspendCancellableCoroutine
        }

        val capture = imageCapture
        if (capture == null) {
            continuation.resume(
                Result.failure(
                    IllegalStateException("La cámara no está inicializada ni vinculada a la vista.")
                )
            )
            return@suspendCancellableCoroutine
        }

        val photosDir = File(appContext.filesDir, "photos").apply {
            if (!exists()) {
                mkdirs()
            }
        }
        val photoFile = File(photosDir, "recipe_${UUID.randomUUID()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val executor = ContextCompat.getMainExecutor(appContext)

        capture.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    continuation.resume(Result.success(photoFile.absolutePath))
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resume(Result.failure(exception))
                }
            }
        )
    }
}
