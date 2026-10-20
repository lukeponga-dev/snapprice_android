package dev.lukeponga.pricesnap.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Docked Ad Banner Space:
 * Standard 52dp mobile ad banner format placed above the navigation bar or docked.
 * Follows Google Play Ad Guidelines: distinct "AD" badge label, clear borders,
 * no deceptive UI, minimum 48dp touch target.
 */
@Composable
fun AdBannerSpace(
    modifier: Modifier = Modifier,
    adText: String = "Promote your vintage store or marketplace listing here",
    adUrl: String = "https://pricesnap.web.app/"
) {
    val context = LocalContext.current

    Surface(
        color = Color(0xFF04120E),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .border(width = 0.5.dp, color = Color(0xFF0E3328))
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adUrl))
                    context.startActivity(intent)
                } catch (_: Exception) {}
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Distinct "AD" badge required by Google Play Policies
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF0E3D31))
                    .border(BorderStroke(0.5.dp, Color(0xFF34D399)), RoundedCornerShape(4.dp))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "AD",
                    color = Color(0xFF34D399),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = "Advertisement",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = adText,
                color = Color(0xFFD1D5DB),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = Icons.Default.OpenInNew,
                contentDescription = null,
                tint = Color(0xFF6B7280),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/**
 * Inline Ad Card Space:
 * Richer native-style card ad space for scrollable content (e.g. Home and Result screens).
 */
@Composable
fun AdCardSpace(
    modifier: Modifier = Modifier,
    sponsorTitle: String = "Featured Thrift Partner",
    sponsorDescription: String = "Discover curated vintage apparel, retro gaming, and certified collectibles nationwide.",
    actionLabel: String = "Explore Store",
    adUrl: String = "https://pricesnap.web.app/"
) {
    val context = LocalContext.current
    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B1E19), Color(0xFF040E0C))
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(BorderStroke(1.dp, Color(0xFF14463A)), RoundedCornerShape(18.dp))
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(adUrl))
                    context.startActivity(intent)
                } catch (_: Exception) {}
            },
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .background(cardGradient)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0E3D31))
                            .border(BorderStroke(0.5.dp, Color(0xFF34D399)), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SPONSORED",
                            color = Color(0xFF34D399),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = sponsorTitle,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    tint = Color(0xFF6B7280),
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = sponsorDescription,
                color = Color(0xFF9CA3AF),
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actionLabel,
                    color = Color(0xFF34D399),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
