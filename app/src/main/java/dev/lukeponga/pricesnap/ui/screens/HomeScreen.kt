package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.R

@Composable
fun HomeScreen(
    totalScans: Int,
    onNavigateToScan: () -> Unit
) {
    val scrollState = rememberScrollState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val compact = maxWidth < 360.dp
        val expanded = maxWidth >= 600.dp
        val horizontalPadding = when {
            expanded -> 32.dp
            compact -> 16.dp
            else -> 20.dp
        }
        val contentWidth = if (expanded) 720.dp else maxWidth
        val titleSize = when {
            expanded -> 44.sp
            compact -> 31.sp
            else -> 36.sp
        }
        val titleLineHeight = when {
            expanded -> 52.sp
            compact -> 38.sp
            else -> 44.sp
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = contentWidth)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = horizontalPadding, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 18.dp else 22.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF072A20),
                border = BorderStroke(1.dp, Color(0xFF0F4E3C)),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = if (compact) 12.dp else 16.dp,
                        vertical = 7.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sparkle),
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "For op shops & individuals",
                        color = Color(0xFF34D399),
                        fontSize = if (compact) 12.sp else 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Appraise anything\n")
                        withStyle(style = SpanStyle(color = Color(0xFF22C55E))) {
                            append("in seconds.")
                        }
                    },
                    color = Color.White,
                    fontSize = titleSize,
                    fontWeight = FontWeight.Normal,
                    lineHeight = titleLineHeight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Fast, grounded price estimates for thrift finds, clothing, collectibles and second-hand goods.",
                    color = Color(0xFF9CA3AF),
                    fontSize = if (expanded) 17.sp else 15.sp,
                    lineHeight = if (expanded) 25.sp else 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .widthIn(max = 580.dp)
                        .padding(horizontal = if (compact) 0.dp else 10.dp)
                )
            }

            Button(
                onClick = onNavigateToScan,
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_scan),
                        contentDescription = null,
                        tint = Color(0xFF042116),
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "Scan an item",
                        color = Color(0xFF042116),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1D19)),
                border = BorderStroke(1.dp, Color(0xFF143029))
            ) {
                Column(
                    modifier = Modifier
                        .padding(if (compact) 18.dp else 22.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "How PriceSnap works",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        HowItWorksStep("1", "Snap the details", "Capture the item, label, tag or hallmark.")
                        HowItWorksStep("2", "AI checks the market", "PriceSnap compares visual details with resale signals.")
                        HowItWorksStep("3", "Review your estimate", "See an NZD range, confidence and condition notes.")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HowItWorksStep(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFF0E3D31), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color(0xFF34D399),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
            Text(
                text = description,
                color = Color(0xFF9CA3AF),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
