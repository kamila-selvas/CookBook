package com.example.cookbook

import com.example.cookbook.domain.usecase.CapturePhotoUseCase
import com.example.cookbook.domain.usecase.IsCameraAvailableUseCase
import com.example.cookbook.ui.recipes.CameraViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakePhotoRepo: FakePhotoRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakePhotoRepo = FakePhotoRepository(isCameraAvailable = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(cameraAvailable: Boolean = true): CameraViewModel {
        fakePhotoRepo.isCameraAvailable = cameraAvailable
        val capturePhotoUseCase = CapturePhotoUseCase(fakePhotoRepo)
        val isCameraAvailableUseCase = IsCameraAvailableUseCase(fakePhotoRepo)
        return CameraViewModel(
            capturePhotoUseCase = capturePhotoUseCase,
            isCameraAvailableUseCase = isCameraAvailableUseCase
        )
    }

    @Test
    fun `initial state reflects camera availability`() {
        val viewModelAvailable = createViewModel(cameraAvailable = true)
        assertTrue(viewModelAvailable.uiState.value.isCameraAvailable)

        val viewModelUnavailable = createViewModel(cameraAvailable = false)
        assertFalse(viewModelUnavailable.uiState.value.isCameraAvailable)
    }

    @Test
    fun `permission granted updates state with permission active`() {
        val viewModel = createViewModel()

        viewModel.onPermissionResult(isGranted = true, canRequestAgain = false)

        val state = viewModel.uiState.value
        assertTrue(state.hasCameraPermission)
        assertFalse(state.shouldShowRationale)
        assertFalse(state.isPermanentlyDenied)
        assertNull(state.errorMessage)
    }

    @Test
    fun `permission denied temporarily updates state with rationale flag`() {
        val viewModel = createViewModel()

        viewModel.onPermissionResult(isGranted = false, canRequestAgain = true)

        val state = viewModel.uiState.value
        assertFalse(state.hasCameraPermission)
        assertTrue(state.shouldShowRationale)
        assertFalse(state.isPermanentlyDenied)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `permission permanently denied updates state with permanent denial flag`() {
        val viewModel = createViewModel()

        viewModel.onPermissionResult(isGranted = false, canRequestAgain = false)

        val state = viewModel.uiState.value
        assertFalse(state.hasCameraPermission)
        assertFalse(state.shouldShowRationale)
        assertTrue(state.isPermanentlyDenied)
        assertNotNull(state.errorMessage)
    }

    @Test
    fun `takePhoto successfully saves photo and updates capturedPhotoPath`() = runTest {
        val viewModel = createViewModel()
        viewModel.onPermissionResult(isGranted = true, canRequestAgain = false)

        fakePhotoRepo.nextPhotoPath = "/data/user/0/photos/tacos_al_pastor.jpg"
        viewModel.takePhoto()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isCapturing)
        assertEquals("/data/user/0/photos/tacos_al_pastor.jpg", state.capturedPhotoPath)
        assertNull(state.errorMessage)
        assertEquals(1, fakePhotoRepo.takePhotoCallCount)
    }

    @Test
    fun `takePhoto handles failure gracefully updating errorMessage without crashing`() = runTest {
        val viewModel = createViewModel()
        viewModel.onPermissionResult(isGranted = true, canRequestAgain = false)

        fakePhotoRepo.shouldFail = true
        fakePhotoRepo.failureException = RuntimeException("Sensor I/O error")

        viewModel.takePhoto()
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isCapturing)
        assertNull(state.capturedPhotoPath)
        assertEquals("Sensor I/O error", state.errorMessage)
        assertEquals(1, fakePhotoRepo.takePhotoCallCount)
    }

    @Test
    fun `takePhoto when camera is not available sets error and does not invoke repository`() = runTest {
        val viewModel = createViewModel(cameraAvailable = false)
        viewModel.onPermissionResult(isGranted = true, canRequestAgain = false)

        viewModel.takePhoto()
        runCurrent()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals(0, fakePhotoRepo.takePhotoCallCount)
    }

    @Test
    fun `takePhoto when permission is not granted sets error and does not invoke repository`() = runTest {
        val viewModel = createViewModel(cameraAvailable = true)
        // No se otorga permiso (hasCameraPermission = false)

        viewModel.takePhoto()
        runCurrent()

        val state = viewModel.uiState.value
        assertNotNull(state.errorMessage)
        assertEquals(0, fakePhotoRepo.takePhotoCallCount)
    }

    @Test
    fun `onPhotoHandled resets capturedPhotoPath`() = runTest {
        val viewModel = createViewModel()
        viewModel.onPermissionResult(isGranted = true, canRequestAgain = false)

        viewModel.takePhoto()
        runCurrent()
        assertNotNull(viewModel.uiState.value.capturedPhotoPath)

        viewModel.onPhotoHandled()
        assertNull(viewModel.uiState.value.capturedPhotoPath)
    }
}
