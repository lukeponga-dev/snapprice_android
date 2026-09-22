package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.history.SyncStatus
import dev.lukeponga.pricesnap.model.User
import dev.lukeponga.pricesnap.ui.screens.settings.*

@Composable
fun SettingsScreen(
    currentUser: User? = null,
    userEmail: String? = null,
    isGuest: Boolean = false,
    scanCount: Int = 0,
    syncStatus: SyncStatus = SyncStatus.Idle,
    onClearHistory: () -> Unit = {},
    onLogout: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    onUpdateDisplayName: (String, (Result<Unit>) -> Unit) -> Unit = { _, _ -> },
    onUpdatePassword: (String, (Result<Unit>) -> Unit) -> Unit = { _, _ -> },
    onSendPasswordReset: (String, (Result<Unit>) -> Unit) -> Unit = { _, _ -> },
    onDeleteAccount: ((Result<Unit>) -> Unit) -> Unit = { _ -> }
) {
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    // Practical Settings State
    var currency by rememberSaveable { mutableStateOf("NZD ($)") }
    var region by rememberSaveable { mutableStateOf("New Zealand") }
    var defaultCondition by rememberSaveable { mutableStateOf("Used") }
    var autoSaveScans by rememberSaveable { mutableStateOf(true) }
    var cameraFlash by rememberSaveable { mutableStateOf("Auto") }
    var hapticFeedback by rememberSaveable { mutableStateOf(true) }
    var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

    // Dialog Visibility States
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showRegionDialog by remember { mutableStateOf(false) }
    var showConditionDialog by remember { mutableStateOf(false) }
    var showFlashDialog by remember { mutableStateOf(false) }

    // Privacy & Support States
    var showManageDataDialog by remember { mutableStateOf(false) }
    var showPhotoRetentionDialog by remember { mutableStateOf(false) }
    var showAiDisclosureDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showDataDeletionPolicyDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showReportIssueDialog by remember { mutableStateOf(false) }
    var showHelpCenterDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showContactSupportDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showLicencesDialog by remember { mutableStateOf(false) }
    var showWebsiteDialog by remember { mutableStateOf(false) }

    // Account & Profile States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showCrossDeviceSyncDialog by remember { mutableStateOf(false) }
    var showSignOutConfirmDialog by remember { mutableStateOf(false) }
    var showDeleteAccountConfirmDialog by remember { mutableStateOf(false) }

    // Rotation animation for sync
    val infiniteTransition = rememberInfiniteTransition(label = "syncSpin")
    val syncRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "syncRotation"
    )

    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color(0xFF030E0B),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF030E0B))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Settings",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Configure your valuation engine & account",
                    color = Color(0xFF6B8078),
                    fontSize = 13.sp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile & Account Hero
            ProfileHeroCard(
                currentUser = currentUser,
                userEmail = userEmail,
                isGuest = isGuest,
                scanCount = scanCount,
                syncStatus = syncStatus,
                syncRotation = syncRotation,
                onLogout = onLogout,
                onSyncNow = onSyncNow,
                onEditName = { showEditProfileDialog = true }
            )

            // Account & Cloud Sync Group
            SettingsGroup(title = "Account & Cloud Sync") {
                if (!isGuest) {
                    SettingActionRow(
                        label = "Profile name",
                        value = currentUser?.displayName ?: "Set name",
                        icon = Icons.Default.Badge,
                        onClick = { showEditProfileDialog = true }
                    )
                    SettingsDivider()
                    SettingActionRow(
                        label = "Email address",
                        value = userEmail,
                        icon = Icons.Default.Email,
                        onClick = {
                            if (!userEmail.isNullOrBlank()) {
                                clipboardManager.setText(AnnotatedString(userEmail))
                                snackbarMessage = "Email copied to clipboard"
                            }
                        }
                    )
                    SettingsDivider()
                    SettingActionRow(
                        label = "Cross-device access",
                        value = "Active",
                        icon = Icons.Default.Devices,
                        onClick = { showCrossDeviceSyncDialog = true }
                    )
                    SettingsDivider()
                    SettingActionRow(
                        label = "Security & password",
                        value = "Manage",
                        icon = Icons.Default.Lock,
                        onClick = { showSecurityDialog = true }
                    )
                    SettingsDivider()
                    SettingActionRow(
                        label = "Sign out",
                        icon = Icons.Default.Logout,
                        iconTint = Color(0xFFF87171),
                        onClick = { showSignOutConfirmDialog = true }
                    )
                    SettingsDivider()
                    SettingActionRow(
                        label = "Delete account & data",
                        icon = Icons.Default.DeleteForever,
                        iconTint = Color(0xFFEF4444),
                        onClick = { showDeleteAccountConfirmDialog = true }
                    )
                } else {
                    SettingActionRow(
                        label = "Sign in / Register",
                        value = "Free Cloud Sync",
                        icon = Icons.Default.Login,
                        onClick = onLogout
                    )
                }
            }

            // Appraisal Engine Group
            SettingsGroup(title = "Appraisal Engine") {
                SettingActionRow(
                    label = "Default Currency",
                    value = currency,
                    icon = Icons.Default.Payments,
                    onClick = { showCurrencyDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Market Region",
                    value = region,
                    icon = Icons.Default.Public,
                    onClick = { showRegionDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Default Condition",
                    value = defaultCondition,
                    icon = Icons.Default.History,
                    onClick = { showConditionDialog = true }
                )
                SettingsDivider()
                SettingSwitchRow(
                    label = "Auto-save scans",
                    subtitle = "Store valuation history locally",
                    icon = Icons.Default.Save,
                    checked = autoSaveScans,
                    onCheckedChange = { autoSaveScans = it }
                )
            }

            // Camera & Feedback Group
            SettingsGroup(title = "Camera & Interface") {
                SettingActionRow(
                    label = "Camera Flash",
                    value = cameraFlash,
                    icon = Icons.Default.FlashOn,
                    onClick = { showFlashDialog = true }
                )
                SettingsDivider()
                SettingSwitchRow(
                    label = "Haptic Feedback",
                    subtitle = "Vibrate on successful scan",
                    icon = Icons.Default.Vibration,
                    checked = hapticFeedback,
                    onCheckedChange = { hapticFeedback = it }
                )
                SettingsDivider()
                SettingSwitchRow(
                    label = "Valuation Notifications",
                    icon = Icons.Default.Notifications,
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }

            // Privacy & Data Group
            SettingsGroup(title = "Privacy & Data") {
                SettingActionRow(
                    label = "Manage scan data",
                    icon = Icons.Default.Storage,
                    onClick = { showManageDataDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Photo retention policy",
                    icon = Icons.Default.Security,
                    onClick = { showPhotoRetentionDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "AI processing disclosure",
                    icon = Icons.Default.AutoAwesome,
                    onClick = { showAiDisclosureDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Privacy Policy",
                    icon = Icons.Default.Policy,
                    onClick = { showPrivacyPolicyDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Clear scan history",
                    icon = Icons.Default.DeleteSweep,
                    iconTint = Color(0xFFEF4444),
                    onClick = { showDeleteConfirmDialog = true }
                )
            }

            // Support & About Group
            SettingsGroup(title = "Support & About") {
                SettingActionRow(
                    label = "Help Centre & FAQ",
                    icon = Icons.Default.HelpCenter,
                    onClick = { showHelpCenterDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Report an issue",
                    icon = Icons.Default.BugReport,
                    onClick = { showReportIssueDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Send feedback",
                    icon = Icons.Default.Feedback,
                    onClick = { showFeedbackDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Terms of Service",
                    icon = Icons.Default.Description,
                    onClick = { showTermsDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Open Source Licences",
                    icon = Icons.Default.Code,
                    onClick = { showLicencesDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "PriceSnap Website",
                    icon = Icons.Default.Language,
                    onClick = { showWebsiteDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "PriceSnap v1.0.4 Prototype\nBuilt by Luke Ponga",
                color = Color(0xFF374151),
                fontSize = 11.sp,
                lineHeight = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
            )
        }
    }

    // Handle Dialogs
    if (showCurrencyDialog) {
        SingleChoiceListDialog(
            title = "Default Currency",
            options = listOf("NZD ($)", "AUD ($)", "USD ($)", "GBP (£)", "EUR (€)"),
            selectedOption = currency,
            onOptionSelected = { currency = it; showCurrencyDialog = false },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showRegionDialog) {
        SingleChoiceListDialog(
            title = "Market Region",
            options = listOf("New Zealand", "Australia", "United States", "United Kingdom", "Global"),
            selectedOption = region,
            onOptionSelected = { region = it; showRegionDialog = false },
            onDismiss = { showRegionDialog = false }
        )
    }

    if (showConditionDialog) {
        SingleChoiceListDialog(
            title = "Default Condition",
            options = listOf("Brand New", "Like New", "Used", "Heavily Used", "Damaged"),
            selectedOption = defaultCondition,
            onOptionSelected = { defaultCondition = it; showConditionDialog = false },
            onDismiss = { showConditionDialog = false }
        )
    }

    if (showFlashDialog) {
        SingleChoiceListDialog(
            title = "Camera Flash",
            options = listOf("Auto", "On", "Off"),
            selectedOption = cameraFlash,
            onOptionSelected = { cameraFlash = it; showFlashDialog = false },
            onDismiss = { showFlashDialog = false }
        )
    }

    // Account Dialogs
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = currentUser?.displayName ?: "",
            onDismiss = { showEditProfileDialog = false },
            onSave = onUpdateDisplayName
        )
    }

    if (showSecurityDialog) {
        SecurityDialog(
            userEmail = userEmail,
            onDismiss = { showSecurityDialog = false },
            onSendReset = onSendPasswordReset,
            onUpdatePassword = onUpdatePassword,
            onResult = { snackbarMessage = it }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = Color(0xFF1E2224),
            title = { Text("Clear History?", color = Color.White) },
            text = { Text("All local scan records will be permanently removed.", color = Color(0xFF9CA3AF)) },
            confirmButton = {
                TextButton(onClick = { onClearHistory(); showDeleteConfirmDialog = false }) {
                    Text("Clear All", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        )
    }

    if (showPhotoRetentionDialog) {
        PhotoRetentionDialog(onDismiss = { showPhotoRetentionDialog = false })
    }

    if (showAiDisclosureDialog) {
        AiDisclosureDialog(onDismiss = { showAiDisclosureDialog = false })
    }

    if (showReportIssueDialog) {
        ReportIssueDialog(onDismiss = { showReportIssueDialog = false }, onSubmitted = { snackbarMessage = it; showReportIssueDialog = false })
    }

    if (showHelpCenterDialog) {
        HelpCenterDialog(onDismiss = { showHelpCenterDialog = false })
    }

    if (showContactSupportDialog) {
        ContactSupportDialog(onDismiss = { showContactSupportDialog = false })
    }

    if (showTermsDialog) {
        TermsDialog(onDismiss = { showTermsDialog = false })
    }

    if (showLicencesDialog) {
        LicencesDialog(onDismiss = { showLicencesDialog = false })
    }

    if (showWebsiteDialog) {
        WebsiteDialog(onDismiss = { showWebsiteDialog = false })
    }

    if (showManageDataDialog) {
        InfoDialog(
            title = "Manage Scan Data",
            icon = Icons.Default.Storage,
            content = {
                Text("PriceSnap stores records locally and syncs them to Firebase Cloud for authenticated users.", color = Color(0xFFD1D5DB))
            },
            onDismiss = { showManageDataDialog = false }
        )
    }

    if (showPrivacyPolicyDialog) {
        InfoDialog(
            title = "Privacy Policy",
            icon = Icons.Default.Lock,
            content = {
                Text(LegalPolicies.PRIVACY_POLICY, color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState()))
            },
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    if (showDataDeletionPolicyDialog) {
        InfoDialog(
            title = "Data Deletion Policy",
            icon = Icons.Default.DeleteSweep,
            content = {
                Text(LegalPolicies.DATA_DELETION_POLICY, color = Color(0xFFD1D5DB), fontSize = 13.sp, modifier = Modifier.heightIn(max = 300.dp).verticalScroll(rememberScrollState()))
            },
            onDismiss = { showDataDeletionPolicyDialog = false }
        )
    }

    if (showCrossDeviceSyncDialog) {
        InfoDialog(
            title = "Cross-Device Synchronization",
            icon = Icons.Default.Devices,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Access your scans anywhere", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text("Your scan history and valuations are automatically synchronized to your personal Firebase Cloud account, ensuring real-time updates across all your devices.", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                }
            },
            onDismiss = { showCrossDeviceSyncDialog = false }
        )
    }

    if (showSignOutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirmDialog = false },
            containerColor = Color(0xFF1E2224),
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(28.dp)) },
            title = { Text("Sign Out?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Your scans are safe in the cloud and will be restored when you sign back in.", color = Color(0xFFD1D5DB), fontSize = 14.sp) },
            confirmButton = {
                Button(onClick = { showSignOutConfirmDialog = false; onLogout() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), shape = RoundedCornerShape(12.dp)) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirmDialog = false }) { Text("Cancel", color = Color(0xFF9CA3AF)) }
            }
        )
    }

    if (showDeleteAccountConfirmDialog) {
        var isDeleting by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteAccountConfirmDialog = false },
            containerColor = Color(0xFF1E2224),
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(32.dp)) },
            title = { Text("Delete Account?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("This is permanent. All your saved valuations and account data will be purged from our servers.", color = Color(0xFFD1D5DB), fontSize = 14.sp, lineHeight = 20.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        onDeleteAccount { result ->
                            isDeleting = false
                            showDeleteAccountConfirmDialog = false
                            if (result.isFailure) snackbarMessage = result.exceptionOrNull()?.localizedMessage ?: "Failed to delete account."
                        }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isDeleting) "Deleting..." else "Permanently Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirmDialog = false }, enabled = !isDeleting) { Text("Cancel", color = Color(0xFF9CA3AF)) }
            }
        )
    }

    if (snackbarMessage != null) {
        LaunchedEffect(snackbarMessage) {
            // In a real app we'd use SnackbarHostState, here we just show/hide
            kotlinx.coroutines.delay(3000)
            snackbarMessage = null
        }
    }
}
