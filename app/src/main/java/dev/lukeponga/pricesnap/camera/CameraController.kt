package dev.lukeponga.pricesnap.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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

    /**
     * Binds the CameraX life-cycle to the provided PreviewView using the device's rear camera.
     */
    fun bindCameraPreview(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        imageCapture: ImageCapture
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            // Configure preview stream
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            // Enforce rear-camera preference for scanning physical items
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                // Handle camera binding exceptions gracefully
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Unbinds all CameraX use cases from the provider.
     */
    fun unbindCamera(context: Context) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        try {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
        } catch (e: Exception) {
            // Error getting camera provider
        }
    }

    /**
     * Captures a high-resolution image from the active CameraX stream, converts it to a Bitmap,
     * and encodes it into the Base64 data string required by the backend gateway.
     */
    suspend fun captureAndEncodeImage(
        imageCapture: ImageCapture,
        context: Context
    ): String = suspendCoroutine { continuation ->
        val executor: Executor = ContextCompat.getMainExecutor(context)

        imageCapture.takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    try {
                        val bitmap = imageProxyToBitmap(image)
                        image.close()

                        // Compress and encode to Base64 data URI format
                        val base64String = compressAndEncodeBitmap(bitmap)
                        continuation.resume(base64String)
                    } catch (e: Exception) {
                        image.close()
                        continuation.resumeWithException(e)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    continuation.resumeWithException(exception)
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun compressAndEncodeBitmap(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Compress to JPEG at 85% quality to stay well under the 15MB backend ceiling
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64Encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64Encoded"
    }
}
