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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * AdMob Configuration Constants
 * App ID: ca-app-pub-4171876147164529~7401775961
 * Ad Unit: banner bottom homepageca-app-pub-4171876147164529/9680629955
 */
object AdMobConfig {
    const val APP_ID = "ca-app-pub-4171876147164529~7401775961"
    const val BANNER_BOTTOM_HOMEPAGE = "ca-app-pub-4171876147164529/9680629955"
}

/**
 * Docked Ad Banner Space:
 * Integrates Google Mobile Ads SDK AdView using banner unit: ca-app-pub-4171876147164529/9680629955
 * Placement: Bottom Homepage
 * Type: Banner
 * Size: Standard Banner (320x50 dp, AdSize.BANNER)
 *
 * Strictly adheres to AdMob and Google Play policies:
 * - Clear boundaries, minimum 48dp touch targets, no deceptive clicks.
 * - Non-overlapping layout to prevent accidental clicks.
 * - Proper lifecycle cleanup via onRelease { it.destroy() }.
 * - Fixed reserved height (50.dp) to eliminate layout shift (CLS).
 */
@Composable
fun AdBannerSpace(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.BANNER_BOTTOM_HOMEPAGE,
    fallbackText: String = "Promote your vintage store or marketplace listing here",
    fallbackUrl: String = "https://pricesnap.web.app/"
) {
    var adLoaded by remember { mutableStateOf(false) }
    var adFailed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        color = Color(0xFF04120E),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .border(width = 0.5.dp, color = Color(0xFF0E3328))
            .testTag("ad_banner_container")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                adFailed -> {
                    // Safe, policy-compliant fallback house ad / partner listing when ad fill fails
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                                    context.startActivity(intent)
                                } catch (_: Exception) {}
                            }
                            .padding(horizontal = 14.dp)
                            .testTag("ad_fallback_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
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
                            text = fallbackText,
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
                else -> {
                    // Google Mobile Ads SDK AdView
                    AndroidView(
                        modifier = Modifier
                            .wrapContentSize(Alignment.Center)
                            .testTag("admob_banner_view"),
                        factory = { ctx ->
                            AdView(ctx).apply {
                                setAdSize(AdSize.BANNER)
                                this.adUnitId = adUnitId
                                adListener = object : AdListener() {
                                    override fun onAdLoaded() {
                                        super.onAdLoaded()
                                        adLoaded = true
                                        adFailed = false
                                    }

                                    override fun onAdFailedToLoad(error: LoadAdError) {
                                        super.onAdFailedToLoad(error)
                                        adFailed = true
                                        adLoaded = false
                                    }
                                }
                                loadAd(AdRequest.Builder().build())
                            }
                        },
                        onRelease = { adView ->
                            adView.destroy()
                        }
                    )
                }
            }
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
            }
            .testTag("ad_card_space"),
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
