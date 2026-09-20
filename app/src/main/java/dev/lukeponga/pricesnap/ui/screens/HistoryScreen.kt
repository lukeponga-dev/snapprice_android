package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.lukeponga.pricesnap.history.HistoryEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    hasScans: Boolean,
    historyItems: List<HistoryEntity> = emptyList(),
    syncStatus: dev.lukeponga.pricesnap.history.SyncStatus = dev.lukeponga.pricesnap.history.SyncStatus.Idle,
    onDeleteItem: (HistoryEntity) -> Unit = {},
    onStartScanning: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        if (!hasScans || historyItems.isEmpty()) {
            // Empty State Matching Screenshot 3
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Isometric 3D Cardboard Box Icon in Warm Amber
                    IsometricBoxIcon(
                        modifier = Modifier.size(72.dp),
                        color = Color(0xFFF59E0B)
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "No scans yet",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Your saved appraisal history will appear here.",
                        color = Color(0xFF9CA3AF),
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onStartScanning,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                        modifier = Modifier
                            .height(52.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(18.dp),
                                spotColor = Color(0xFF10B981).copy(alpha = 0.5f),
                                ambientColor = Color(0xFF10B981).copy(alpha = 0.4f)
                            ),
                        contentPadding = PaddingValues(horizontal = 32.dp)
                    ) {
                        Text(
                            text = "Start scanning",
                            color = Color(0xFF042116),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Active History List Matching the Dark Slate Palette
            val dateFormat = remember { SimpleDateFormat("d MMM yyyy, h:mm a", Locale.getDefault()) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Saved Appraisals",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (syncStatus is dev.lukeponga.pricesnap.history.SyncStatus.Syncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = Color(0xFF10B981),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF072A20),
                            border = BorderStroke(1.dp, Color(0xFF0F4E3C))
                        ) {
                            Text(
                                text = "${historyItems.size} items",
                                color = Color(0xFF34D399),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                items(historyItems, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
                        border = BorderStroke(1.dp, Color(0xFF143029))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Thumbnail Preview
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF05120F)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.itemName,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = null,
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.itemName,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF072A20)
                                    ) {
                                        Text(
                                            text = item.condition,
                                            color = Color(0xFF34D399),
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = item.category,
                                        color = Color(0xFF9CA3AF),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateFormat.format(Date(item.date)),
                                    color = Color(0xFF6B7280),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                                // Valuation Price & Delete
                                val confidenceColor = when {
                                    item.confidence >= 0.8 -> Color(0xFF22C55E)
                                    item.confidence >= 0.4 -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "NZ$${String.format(Locale.US, "%.0f", item.price)}",
                                        color = Color(0xFF22C55E),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${(item.confidence * 100).toInt()}% conf.",
                                        color = confidenceColor,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3D Isometric Open Cardboard Box Vector matching Screenshot 3
 */
@Composable
fun IsometricBoxIcon(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFF59E0B)
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 3.5.dp.toPx()
        val w = size.width
        val h = size.height

        // Center / key vertices of isometric box
        val cx = w * 0.5f
        val rimTopY = h * 0.36f
        val rimLeftX = w * 0.16f
        val rimRightX = w * 0.84f
        val rimMidY = h * 0.52f
        val rimBottomY = h * 0.68f

        val botLeftX = rimLeftX
        val botRightX = rimRightX
        val botCenterY = h * 0.94f
        val botSideY = h * 0.78f

        val strokeStyle = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        // 1. Box Bottom Edges & Vertical Creases
        val boxBody = Path().apply {
            // Left vertical edge
            moveTo(rimLeftX, rimMidY)
            lineTo(botLeftX, botSideY)
            // Left bottom slant to bottom center
            lineTo(cx, botCenterY)
            // Right bottom slant to bottom right
            lineTo(botRightX, botSideY)
            // Right vertical edge
            lineTo(rimRightX, rimMidY)

            // Center vertical crease
            moveTo(cx, rimBottomY)
            lineTo(cx, botCenterY)
        }
        drawPath(path = boxBody, color = color, style = strokeStyle)

        // 2. Open Rim (Bottom Diamond)
        val rimPath = Path().apply {
            moveTo(rimLeftX, rimMidY)
            lineTo(cx, rimBottomY)
            lineTo(rimRightX, rimMidY)
        }
        drawPath(path = rimPath, color = color, style = strokeStyle)

        // 3. Open Flaps
        // Left Flap (angled up-left)
        val leftFlap = Path().apply {
            moveTo(rimLeftX, rimMidY)
            lineTo(w * 0.04f, h * 0.38f)
            lineTo(w * 0.28f, h * 0.22f)
            lineTo(cx, rimTopY)
        }
        drawPath(path = leftFlap, color = color, style = strokeStyle)

        // Right Flap (angled up-right)
        val rightFlap = Path().apply {
            moveTo(rimRightX, rimMidY)
            lineTo(w * 0.96f, h * 0.38f)
            lineTo(w * 0.72f, h * 0.22f)
            lineTo(cx, rimTopY)
        }
        drawPath(path = rightFlap, color = color, style = strokeStyle)

        // Front-Left Flap
        val frontLeftFlap = Path().apply {
            moveTo(rimLeftX, rimMidY)
            lineTo(w * 0.26f, h * 0.62f)
            lineTo(cx, rimBottomY)
        }
        drawPath(path = frontLeftFlap, color = color, style = strokeStyle)

        // Front-Right Flap
        val frontRightFlap = Path().apply {
            moveTo(rimRightX, rimMidY)
            lineTo(w * 0.74f, h * 0.62f)
            lineTo(cx, rimBottomY)
        }
        drawPath(path = frontRightFlap, color = color, style = strokeStyle)
    }
}
