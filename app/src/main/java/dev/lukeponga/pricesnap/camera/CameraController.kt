package dev.lukeponga.pricesnap.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object CameraController {
    private var camera: Camera? = null
    var isFlashOn: Boolean = false
        private set

    fun bindCameraPreview(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        imageCapture: ImageCapture,
        onError: (Throwable) -> Unit = {}
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                cameraProvider.unbindAll()
                val boundCamera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
                camera = boundCamera
                if (isFlashOn) {
                    runCatching {
                        if (boundCamera.cameraInfo.hasFlashUnit()) {
                            boundCamera.cameraControl.enableTorch(true)
                        }
                        imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
                    }
                }
            } catch (exc: Exception) {
                onError(exc)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun setFlashEnabled(enabled: Boolean, imageCapture: ImageCapture? = null) {
        isFlashOn = enabled
        imageCapture?.flashMode = if (enabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        camera?.let { cam ->
            runCatching {
                if (cam.cameraInfo.hasFlashUnit()) {
                    cam.cameraControl.enableTorch(enabled)
                }
            }
        }
    }

    fun unbindCamera(context: Context) {
        runCatching {
            camera?.cameraControl?.enableTorch(false)
        }
        camera = null
        isFlashOn = false
        val future = ProcessCameraProvider.getInstance(context)
        future.addListener({
            runCatching { future.get().unbindAll() }
        }, ContextCompat.getMainExecutor(context))
    }

    suspend fun captureAndEncodeImage(
        imageCapture: ImageCapture,
        context: Context
    ): String = suspendCoroutine { continuation ->
        val executor: Executor = ContextCompat.getMainExecutor(context)
        imageCapture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val bitmap = imageProxyToBitmap(image)
                    image.close()
                    continuation.resume(compressAndEncodeBitmap(bitmap))
                } catch (e: Exception) {
                    image.close()
                    continuation.resumeWithException(e)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                continuation.resumeWithException(exception)
            }
        })
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return requireNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)) {
            "Camera image could not be decoded"
        }
    }

    private fun compressAndEncodeBitmap(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val base64Encoded = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64Encoded"
    }
}
