package dev.lukeponga.pricesnap.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.activity.result.PickVisualMediaRequest
import dev.lukeponga.pricesnap.camera.CameraController
import dev.lukeponga.pricesnap.ui.AppraisalViewModel
import kotlinx.coroutines.launch
import java.io.FileOutputStream

@Composable
fun ScanScreen(
    viewModel: AppraisalViewModel,
    onScanCompleted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val file = java.io.File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                
                viewModel.appraiseImage(file)
                onScanCompleted()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            CameraController.unbindCamera(context)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ) {
        if (hasCameraPermission) {
            // Live CameraX Preview View
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    CameraController.bindCameraPreview(
                        context = context,
                        lifecycleOwner = lifecycleOwner,
                        previewView = previewView,
                        imageCapture = imageCapture
                    )
                }
            )

            // Top Header Instruction
            Text(
                text = "Center item in frame",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp)
            )

            // Framing Viewfinder Box with Green Accents
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
                    .border(BorderStroke(2.dp, Color(0xFF10B981)), RoundedCornerShape(16.dp))
            )

            // Bottom Shutter Button Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                // Upload Button (Bottom Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 32.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val base64Image = CameraController.captureAndEncodeImage(
                                    imageCapture = imageCapture,
                                    context = context
                                )
                                // Save to file for history persistence
                                val file = java.io.File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                                val bytes = android.util.Base64.decode(base64Image.substringAfter(","), android.util.Base64.DEFAULT)
                                file.writeBytes(bytes)
                                
                                // Trigger backend analysis in ViewModel
                                viewModel.analyzeCapturedImage(base64Image, file)
                                onScanCompleted()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Capture failed: ${e.localizedMessage}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.Transparent, CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFF10B981), CircleShape)
                    )
                }
            }
        } else {
            // Permission Denied / Request State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Camera permission is required to scan items for appraisal.",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}
