package dev.lukeponga.pricesnap.ui.screens.settings

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.lukeponga.pricesnap.history.SyncStatus
import dev.lukeponga.pricesnap.model.User
import java.util.Locale

@Composable
fun ProfileHeroCard(
    currentUser: User?,
    userEmail: String?,
    isGuest: Boolean,
    scanCount: Int,
    syncStatus: SyncStatus,
    syncRotation: Float,
    onLogout: () -> Unit,
    onSyncNow: () -> Unit,
    onEditName: () -> Unit
) {
    val rawName = currentUser?.displayName
    val effectiveName = if (!rawName.isNullOrBlank()) {
        rawName
    } else if (!userEmail.isNullOrBlank()) {
        userEmail.substringBefore('@').replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    } else {
        "PriceSnap Member"
    }

    val initials = effectiveName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifBlank { "PS" }

    if (isGuest) {
        GuestProfileCard(scanCount, onLogout)
    } else {
        AuthenticatedProfileCard(
            effectiveName = effectiveName,
            initials = initials,
            userEmail = userEmail,
            syncStatus = syncStatus,
            syncRotation = syncRotation,
            onSyncNow = onSyncNow,
            onEditName = onEditName
        )
    }
}

@Composable
private fun GuestProfileCard(scanCount: Int, onLogout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF101C18),
        border = BorderStroke(1.dp, Color(0xFF1E382E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A2E27)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PersonOutline,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guest User",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B))
                        )
                        Text(
                            text = "Local Storage Only · Not Synced",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daily Free Scans Used", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                Text("$scanCount / 10", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            LinearProgressIndicator(
                progress = { (scanCount / 10f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(6.dp)
                    .clip(CircleShape),
                color = Color(0xFF10B981),
                trackColor = Color(0xFF0C1D19)
            )
            Text(
                "Sign in with Google or Email to synchronize all your scans across devices and unlock unlimited daily valuations!",
                color = Color(0xFF9CA3AF),
                fontSize = 12.5.sp,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Icon(Icons.Default.Login, contentDescription = null, tint = Color(0xFF031612), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign In or Register", color = Color(0xFF031612), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun AuthenticatedProfileCard(
    effectiveName: String,
    initials: String,
    userEmail: String?,
    syncStatus: SyncStatus,
    syncRotation: Float,
    onSyncNow: () -> Unit,
    onEditName: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0D1E18),
        border = BorderStroke(1.dp, Color(0xFF1A3E32))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF059669), Color(0xFF10B981))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = effectiveName,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        IconButton(
                            onClick = onEditName,
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit name",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Text(
                        text = userEmail ?: "No email set",
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    SyncStatusBadge(syncStatus, syncRotation)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onEditName,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E4336)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF34D399))
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Name", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onSyncNow,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        tint = Color(0xFF031612),
                        modifier = Modifier
                            .size(14.dp)
                            .then(if (syncStatus is SyncStatus.Syncing) Modifier.rotate(syncRotation) else Modifier)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (syncStatus is SyncStatus.Syncing) "Syncing..." else "Sync Now",
                        color = Color(0xFF031612),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncStatusBadge(syncStatus: SyncStatus, syncRotation: Float) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = when (syncStatus) {
            is SyncStatus.Syncing -> Color(0xFF0C2B22)
            is SyncStatus.Synced -> Color(0xFF07291E)
            is SyncStatus.Error -> Color(0xFF2E1F0B)
            else -> Color(0xFF07291E)
        },
        border = BorderStroke(
            1.dp,
            when (syncStatus) {
                is SyncStatus.Syncing -> Color(0xFF10B981).copy(alpha = 0.5f)
                is SyncStatus.Synced -> Color(0xFF15533F)
                is SyncStatus.Error -> Color(0xFF78350F)
                else -> Color(0xFF15533F)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            when (syncStatus) {
                is SyncStatus.Syncing -> {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(12.dp).rotate(syncRotation))
                    Text("Syncing scans...", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                is SyncStatus.Synced -> {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(12.dp))
                    Text("Cloud Synced (${syncStatus.itemCount} items)", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                is SyncStatus.Error -> {
                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(12.dp))
                    Text("Sync Offline · Tap Sync Now", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                else -> {
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(12.dp))
                    Text("Cloud Sync Active", color = Color(0xFF34D399), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String, (Result<Unit>) -> Unit) -> Unit
) {
    var nameInput by remember { mutableStateOf(currentName) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF1E2224), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Edit Profile Name", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Your display name is shown on your appraisals across all your devices.", color = Color(0xFF9CA3AF), fontSize = 13.sp)
                
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Display Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF2D3748),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 12.sp)
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancel", color = Color(0xFF9CA3AF)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (nameInput.isNotBlank()) {
                                isSaving = true
                                onSave(nameInput.trim()) { result ->
                                    isSaving = false
                                    if (result.isSuccess) onDismiss() else errorMessage = result.exceptionOrNull()?.message
                                }
                            }
                        },
                        enabled = !isSaving && nameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text(if (isSaving) "Saving..." else "Save", color = Color(0xFF031612), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
