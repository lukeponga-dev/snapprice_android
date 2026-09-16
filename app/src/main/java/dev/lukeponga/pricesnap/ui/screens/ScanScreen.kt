package dev.lukeponga.pricesnap.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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

    val galleryLauncher = rememberLauncherForActivityResult(
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

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            CameraController.unbindCamera(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Camera Viewfinder with Rounded Frame & Neon Corner Brackets
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF0C1D19))
                .border(BorderStroke(1.5.dp, Color(0xFF14463A)), RoundedCornerShape(26.dp))
        ) {
            if (hasCameraPermission) {
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
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Camera access needed",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap to grant camera access",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Enable Camera", color = Color(0xFF042116), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Top-Left and Bottom-Right Neon Green Viewfinder Brackets
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                val strokeWidth = 2.8.dp.toPx()
                val bracketLength = 54.dp.toPx()
                val cornerRadius = 14.dp.toPx()
                val bracketColor = Color(0xFF34D399)

                // Top-Left Corner
                val tlPath = Path().apply {
                    moveTo(0f, bracketLength)
                    lineTo(0f, cornerRadius)
                    quadraticBezierTo(0f, 0f, cornerRadius, 0f)
                    lineTo(bracketLength, 0f)
                }
                drawPath(
                    path = tlPath,
                    color = bracketColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                // Bottom-Right Corner
                val brPath = Path().apply {
                    moveTo(size.width - bracketLength, size.height)
                    lineTo(size.width - cornerRadius, size.height)
                    quadraticBezierTo(size.width, size.height, size.width, size.height - cornerRadius)
                    lineTo(size.width, size.height - bracketLength)
                }
                drawPath(
                    path = brPath,
                    color = bracketColor,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Circular Shutter Button
        Box(
            modifier = Modifier
                .size(76.dp)
                .border(BorderStroke(3.dp, Color(0xFF34D399)), CircleShape)
                .padding(5.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E))
                .clickable {
                    coroutineScope.launch {
                        try {
                            val base64Image = CameraController.captureAndEncodeImage(
                                imageCapture = imageCapture,
                                context = context
                            )
                            val file = java.io.File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
                            val bytes = android.util.Base64.decode(base64Image.substringAfter(","), android.util.Base64.DEFAULT)
                            file.writeBytes(bytes)

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
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.PhotoCamera,
                contentDescription = "Capture photo",
                tint = Color(0xFF042116),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. "Upload a photo" Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0C1D19),
            border = BorderStroke(1.dp, Color(0xFF15382E))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FileUpload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Upload a photo",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. "Better photo, better estimate" Tip Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF0C1D19),
            border = BorderStroke(1.dp, Color(0xFF15382E))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = "Better photo, better estimate",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Use bright, even light and a simple background.",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
