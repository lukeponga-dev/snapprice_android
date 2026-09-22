package dev.lukeponga.pricesnap.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = Color(0xFF6B8078),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1A16)),
            border = BorderStroke(1.dp, Color(0xFF132B25)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        color = Color(0xFF132B25),
        thickness = 0.5.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun SettingActionRow(
    label: String,
    value: String? = null,
    icon: ImageVector,
    iconTint: Color = Color(0xFF10B981),
    labelColor: Color = Color.White,
    chevronTint: Color = Color(0xFF6B7280),
    showChevron: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = label,
            color = labelColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        if (value != null) {
            Text(
                text = value,
                color = Color(0xFF9CA3AF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
            if (showChevron) Spacer(modifier = Modifier.width(8.dp))
        }

        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = chevronTint,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SettingSwitchRow(
    label: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 54.dp)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF10B981).copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = Color(0xFF9CA3AF),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF10B981),
                uncheckedThumbColor = Color(0xFF9CA3AF),
                uncheckedTrackColor = Color(0xFF2D3748)
            )
        )
    }
}
