package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.ui.BackendStatus

@Composable
fun ScanningScreen(
    backendStatus: BackendStatus
) {
    val transition = rememberInfiniteTransition(label = "scanPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanPulseScale"
    )

    val status = when (backendStatus) {
        BackendStatus.Checking -> ScanStatusContent(
            title = "Checking backend",
            detail = "Confirming PriceSnap is ready to process your image…",
            badge = "CONNECTING",
            color = Color(0xFFFBBF24)
        )
        is BackendStatus.Connected -> ScanStatusContent(
            title = "Scanning your item",
            detail = "Image sent securely to ${backendStatus.service}. AI appraisal in progress…",
            badge = "BACKEND ONLINE",
            color = Color(0xFF34D399)
        )
        BackendStatus.Offline -> ScanStatusContent(
            title = "Backend status unavailable",
            detail = "PriceSnap could not confirm the connection, but it is still attempting your appraisal…",
            badge = "OFFLINE",
            color = Color(0xFFF87171)
        )
        is BackendStatus.Error -> ScanStatusContent(
            title = "Backend degraded",
            detail = "${backendStatus.msg}. PriceSnap is still attempting your appraisal…",
            badge = "DEGRADED",
            color = Color(0xFFFBBF24)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
            border = BorderStroke(1.dp, Color(0xFF15382E))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier
                            .size(88.dp)
                            .scale(pulse),
                        shape = CircleShape,
                        color = status.color.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, status.color.copy(alpha = 0.45f))
                    ) {}
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = status.color,
                        strokeWidth = 3.dp
                    )
                    Icon(
                        imageVector = when (backendStatus) {
                            is BackendStatus.Connected -> Icons.Default.Search
                            BackendStatus.Checking -> Icons.Default.CloudQueue
                            BackendStatus.Offline,
                            is BackendStatus.Error -> Icons.Default.CloudOff
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = status.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = status.detail,
                    color = Color(0xFF9CA3AF),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = status.color.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, status.color.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(status.color, CircleShape)
                        )
                        Text(
                            text = status.badge,
                            color = status.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = status.color,
                    trackColor = Color(0xFF15382E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Keep PriceSnap open while the appraisal completes",
                    color = Color(0xFF6B8078),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private data class ScanStatusContent(
    val title: String,
    val detail: String,
    val badge: String,
    val color: Color
)
