package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.model.AppraisalResponse

import androidx.compose.ui.res.painterResource
import dev.lukeponga.pricesnap.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResultScreen(
    appraisal: AppraisalResponse,
    onScanAgain: () -> Unit,
    onSaveResult: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Product Identification Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
            border = BorderStroke(1.dp, Color(0xFF143029))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Confidence Badge & Progress Bar
                val confidence = appraisal.confidence / 100f
                val confidencePercent = appraisal.confidence
                val confidenceColor = when {
                    confidence >= 0.8f -> Color(0xFF22C55E) // Green
                    confidence >= 0.4f -> Color(0xFFF59E0B) // Amber
                    else -> Color(0xFFEF4444) // Red
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = appraisal.item.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    LinearProgressIndicator(
                        progress = { confidence },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = confidenceColor,
                        trackColor = confidenceColor.copy(alpha = 0.2f),
                    )

                    Text(
                        text = "${appraisal.item.brand ?: "Generic"} • ${appraisal.item.category ?: "General"}",
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = confidenceColor.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, confidenceColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sparkle),
                            contentDescription = null,
                            tint = confidenceColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "$confidencePercent% CONFIDENCE",
                            color = confidenceColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 2. Condition & Defects Assessment Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
            border = BorderStroke(1.dp, Color(0xFF143029))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CONDITION",
                            color = Color(0xFF6B8078),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${appraisal.condition.score}",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = " /10",
                                color = Color(0xFF9CA3AF),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                        
                        // Condition Grade Badge
                        val grade = appraisal.condition.grade
                        Text(
                            text = "Grade $grade",
                            color = Color(0xFF34D399),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // Summary Description
                    Text(
                        text = "A thorough assessment based on the visual evidence provided.",
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Defect Chips
                val defects = appraisal.condition.defects
                if (defects.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        defects.forEach { defect ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF072A20),
                                border = BorderStroke(1.dp, Color(0xFF0F4E3C))
                            ) {
                                Text(
                                    text = defect.uppercase(),
                                    color = Color(0xFF34D399),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Recommended Resale Price & Strategy Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
            border = BorderStroke(1.dp, Color(0xFF143029))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "RECOMMENDED RESALE PRICE",
                            color = Color(0xFF6B8078),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.0f", appraisal.valuation.resalePrice)}",
                            color = Color(0xFF22C55E),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "BEST PLATFORM",
                            color = Color(0xFF6B8078),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF072A20),
                            border = BorderStroke(1.dp, Color(0xFF0F4E3C))
                        ) {
                            Text(
                                text = appraisal.market.bestPlatform ?: "Trade Me",
                                color = Color(0xFF34D399),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF143029))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trends),
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Market analysis version: ${appraisal.metadata.version}",
                        color = Color(0xFF9CA3AF),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 4. Marketplace Comparison Section
        Text(
            text = "MARKETPLACE COMPARISONS (${appraisal.valuation.currency})",
            color = Color(0xFF6B8078),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )

        val trademe = appraisal.market.trademe
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
            border = BorderStroke(1.dp, Color(0xFF143029))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Trade Me (NZ)",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF072A20),
                        border = BorderStroke(1.dp, Color(0xFF0F4E3C))
                    ) {
                        Text(
                            text = "$${String.format(java.util.Locale.US, "%.0f", trademe?.median ?: 0.0)} avg",
                            color = Color(0xFF34D399),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Low: \$${String.format(java.util.Locale.US, "%.0f", trademe?.low ?: 0.0)}", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
                    Text(text = "High: \$${String.format(java.util.Locale.US, "%.0f", trademe?.high ?: 0.0)}", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // 5. Bottom Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onScanAgain,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF15382E)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF0C1D19),
                    contentColor = Color.White
                )
            ) {
                Text("Scan Again", fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = onSaveResult,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E))
            ) {
                Text("Save Result", fontWeight = FontWeight.Bold, color = Color(0xFF042116))
            }
        }
    }
}

