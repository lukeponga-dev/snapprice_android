package dev.lukeponga.pricesnap.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog

@Composable
fun InfoDialog(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E2224),
        icon = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = { content() },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Got It", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun SingleChoiceListDialog(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1E2224),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    options.forEach { text ->
                        val isSelected = text == selectedOption
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .selectable(
                                    selected = isSelected,
                                    onClick = { onOptionSelected(text) }
                                )
                                .background(if (isSelected) Color(0xFF064E3B).copy(alpha = 0.4f) else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onOptionSelected(text) },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = text,
                                color = if (isSelected) Color(0xFF10B981) else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF9CA3AF))
                    }
                }
            }
        }
    }
}

@Composable
fun HelpFaqItem(question: String, answer: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF121212))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = question, color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
        Text(text = answer, color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodySmall, lineHeight = 18.sp)
    }
}
