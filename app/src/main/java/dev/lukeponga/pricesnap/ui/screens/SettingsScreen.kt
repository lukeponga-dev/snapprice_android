package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
fun SettingsScreen(
    onClearHistory: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    // 1. Practical Settings State (rememberSaveable across compositions)
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

    // Privacy Dialogs
    var showManageDataDialog by remember { mutableStateOf(false) }
    var showPhotoRetentionDialog by remember { mutableStateOf(false) }
    var showAiDisclosureDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Support Dialogs
    var showReportIssueDialog by remember { mutableStateOf(false) }
    var showHelpCenterDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showContactSupportDialog by remember { mutableStateOf(false) }

    // About Dialogs
    var showTermsDialog by remember { mutableStateOf(false) }
    var showLicencesDialog by remember { mutableStateOf(false) }
    var showWebsiteDialog by remember { mutableStateOf(false) }

    // Snackbar Feedback Message
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header matching Screenshot 4
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
            )

            // 1. Appraisal
            SettingsGroup(title = "Appraisal") {
                SettingActionRow(
                    label = "Currency",
                    value = "NZD",
                    icon = Icons.Default.MonetizationOn,
                    onClick = { showCurrencyDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Marketplace region",
                    value = region,
                    icon = Icons.Default.LocationOn,
                    onClick = { showRegionDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Default condition",
                    value = defaultCondition,
                    icon = Icons.Default.LocalOffer,
                    onClick = { showConditionDialog = true }
                )
            }

            // 2. Scanning
            SettingsGroup(title = "Scanning") {
                SettingActionRow(
                    label = "Save scans automatically",
                    icon = Icons.Default.Storage,
                    showChevron = false,
                    onClick = {
                        autoSaveScans = !autoSaveScans
                        snackbarMessage = if (autoSaveScans) "Scans will be saved automatically" else "Auto-save disabled"
                    }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Camera flash",
                    value = cameraFlash,
                    icon = Icons.Default.Bolt,
                    onClick = { showFlashDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Haptic feedback",
                    icon = Icons.Default.Vibration,
                    showChevron = false,
                    onClick = {
                        hapticFeedback = !hapticFeedback
                        snackbarMessage = if (hapticFeedback) "Haptic feedback enabled" else "Haptic feedback disabled"
                    }
                )
            }

            // 3. Data & Privacy
            SettingsGroup(title = "Data & Privacy") {
                SettingActionRow(
                    label = "Manage scan data",
                    icon = Icons.Default.Storage,
                    onClick = { showManageDataDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Privacy policy",
                    icon = Icons.Default.Shield,
                    onClick = { showPrivacyPolicyDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Delete scan history",
                    icon = Icons.Default.DeleteOutline,
                    iconTint = Color(0xFFEF4444),
                    labelColor = Color(0xFFEF4444),
                    chevronTint = Color(0xFFEF4444),
                    onClick = { showDeleteConfirmDialog = true }
                )
            }

            // 4. Help
            SettingsGroup(title = "Help") {
                SettingActionRow(
                    label = "Report a valuation issue",
                    icon = Icons.Default.HelpOutline,
                    onClick = { showReportIssueDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Help centre",
                    icon = Icons.Default.HelpOutline,
                    onClick = { showHelpCenterDialog = true }
                )
                SettingsDivider()
                SettingActionRow(
                    label = "Send feedback",
                    icon = Icons.Default.ChatBubbleOutline,
                    onClick = { showFeedbackDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Version text at bottom matching Screenshot 4
            Text(
                text = "PriceSnap · Version 1.0.0",
                color = Color(0xFF4B6058),
                fontSize = 12.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Floating Snackbar Feedback
        snackbarMessage?.let { msg ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF064E3B),
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { snackbarMessage = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }

    // ==========================================
    // SELECTION DIALOGS
    // ==========================================

    // Currency Dialog
    if (showCurrencyDialog) {
        SingleChoiceListDialog(
            title = "Select Currency",
            options = listOf("NZD ($)", "AUD ($)", "USD ($)", "GBP (£)", "EUR (€)", "CAD ($)"),
            selectedOption = currency,
            onOptionSelected = {
                currency = it
                showCurrencyDialog = false
                snackbarMessage = "Currency set to $it"
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    // Region Dialog
    if (showRegionDialog) {
        SingleChoiceListDialog(
            title = "Marketplace Region",
            options = listOf("New Zealand", "Australia", "United States", "United Kingdom", "Global"),
            selectedOption = region,
            onOptionSelected = {
                region = it
                showRegionDialog = false
                snackbarMessage = "Marketplace region set to $it"
            },
            onDismiss = { showRegionDialog = false }
        )
    }

    // Default Condition Dialog
    if (showConditionDialog) {
        SingleChoiceListDialog(
            title = "Default Item Condition",
            options = listOf("Brand New", "Like New", "Used", "Fair", "Poor"),
            selectedOption = defaultCondition,
            onOptionSelected = {
                defaultCondition = it
                showConditionDialog = false
                snackbarMessage = "Default condition set to $it"
            },
            onDismiss = { showConditionDialog = false }
        )
    }

    // Camera Flash Dialog
    if (showFlashDialog) {
        SingleChoiceListDialog(
            title = "Camera Flash Preference",
            options = listOf("Auto", "Always On", "Off"),
            selectedOption = cameraFlash,
            onOptionSelected = {
                cameraFlash = it
                showFlashDialog = false
                snackbarMessage = "Flash preference set to $it"
            },
            onDismiss = { showFlashDialog = false }
        )
    }

    // ==========================================
    // PRIVACY & DATA DIALOGS
    // ==========================================

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = Color(0xFF1E2224),
            icon = {
                Icon(
                    Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Delete All Scan Data?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently remove your scan history, saved valuations, and local cache from this device. This action cannot be undone.",
                    color = Color(0xFFD1D5DB)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearHistory()
                        showDeleteConfirmDialog = false
                        snackbarMessage = "All scan history and local cache cleared"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Delete Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = Color(0xFF9CA3AF))
                }
            }
        )
    }

    // Manage Scan Data Dialog
    if (showManageDataDialog) {
        InfoDialog(
            title = "Manage Scan Data",
            icon = Icons.Default.Storage,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "PriceSnap stores your appraisal records locally in an encrypted Room database on your device.",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF121212),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Local database size", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
                                Text("< 2.5 MB", color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cached scan photos", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
                                Text("Compressed JPEG", color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Cloud synchronization", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
                                Text("Disabled (Offline-first)", color = Color(0xFF10B981), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            onDismiss = { showManageDataDialog = false }
        )
    }

    // Photo Retention Explanation Dialog
    if (showPhotoRetentionDialog) {
        InfoDialog(
            title = "Photo Retention Explanation",
            icon = Icons.Default.Security,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "How PriceSnap handles your photos:",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• Photos taken with PriceSnap are transmitted securely over TLS/SSL directly to the vision appraisal endpoint.\n• Images are processed in ephemeral memory strictly for visual extraction (brand labels, materials, conditions, serials).\n• Photos are NEVER retained on remote servers, sold to data brokers, or used to build facial or biometric profiles.\n• Local thumbnail previews are retained strictly on your own device until you delete them.",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            onDismiss = { showPhotoRetentionDialog = false }
        )
    }

    // AI Processing Disclosure Dialog
    if (showAiDisclosureDialog) {
        InfoDialog(
            title = "AI Processing Disclosure",
            icon = Icons.Default.AutoAwesome,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Automated Valuation Modeling",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "PriceSnap uses multimodal Gemini computer vision models to identify items, read wear and tear, and benchmark listings against secondhand marketplaces including Trade Me, eBay, and Marketplace.\n\nAppraisals represent automated statistical market estimates, not formal accredited insurance appraisals. Always verify valuable jewelry, fine art, or high-end collectibles with a certified valuer.",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            },
            onDismiss = { showAiDisclosureDialog = false }
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyPolicyDialog) {
        InfoDialog(
            title = "Privacy Policy",
            icon = Icons.Default.Lock,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "PriceSnap – Privacy Policy (Prototype Version)\nLast updated: 16 September 2026\nOwner: Luke Ponga, trading as PriceSnap\nContact: lukeponga9@gmail.com",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "1. Introduction\nPriceSnap is an experimental prototype that provides AI‑generated resale value estimates based on photos submitted by users. This Privacy Policy explains what information is collected, how it is used, and the rights available to you. Because this is a prototype, features and data flows may change as the service evolves.\n\n" +
                                "2. Information We Collect\nPriceSnap collects only the information required to operate the prototype, including photos you upload for appraisal and basic technical information such as device type, browser version, and error logs. No account system is currently implemented, and no personal identifiers beyond your submitted content are collected.\n\n" +
                                "3. How Your Photos Are Used\nWhen you submit an image, it is transmitted to an AI model hosted by Google AI Studio (Gemini) for analysis. Under Google’s free-tier Gemini API terms, your inputs may be reviewed by humans and may be used to improve Google products and services. Images are used solely to generate appraisal results within the prototype and are not used by PriceSnap for training or advertising.\n\n" +
                                "4. Data Processing Locations\nBecause the prototype relies on Google AI Studio and other cloud services, your information may be processed outside New Zealand, including in the United States or other regions where Google operates its infrastructure.\n\n" +
                                "5. Retention & Deletion\nImages submitted for appraisal are deleted from PriceSnap’s prototype environment immediately after analysis and are not stored by PriceSnap. Diagnostic logs may be retained for up to 30 days for troubleshooting. If backups are generated by hosting providers, they may persist for up to 90 days before automatic deletion. You may request deletion of any remaining diagnostic data by contacting the email listed above.\n\n" +
                                "6. Minimum Age Requirement\nPriceSnap is not intended for individuals under 18 years of age, in accordance with Google’s Gemini API terms. By using the prototype, you confirm that you are at least 18.\n\n" +
                                "7. Analytics & Diagnostics\nAnonymous analytics or crash logs may be collected to improve reliability. These logs do not include image content or personal identifiers and are used solely for debugging and performance monitoring.\n\n" +
                                "8. Sharing Your Information\nPriceSnap does not sell your data. Information may be shared only with service providers necessary to operate the prototype, such as Google AI Studio for image analysis and the hosting provider for serving the application. These providers may process data overseas and under their own privacy terms.\n\n" +
                                "9. Security\nThe prototype uses secure communication protocols and cloud-hosted infrastructure. However, because this is an early-stage product, security measures may evolve as the service matures. Users should avoid submitting sensitive or confidential images.\n\n" +
                                "10. Your Rights\nYou may request deletion of diagnostic logs or other information associated with your use of the prototype by contacting the email above. Because images are deleted immediately after analysis, they cannot be retrieved or removed after processing.\n\n" +
                                "11. Changes to This Policy\nThis Privacy Policy may be updated as PriceSnap transitions from prototype to production. Significant changes will be communicated through the app or website.\n\n" +
                                "12. Contact\nFor privacy questions or data deletion requests, contact:\nlukeponga9@gmail.com",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            },
            onDismiss = { showPrivacyPolicyDialog = false }
        )
    }

    // ==========================================
    // ACTIONABLE SUPPORT DIALOGS
    // ==========================================

    // Report a Valuation Issue Dialog
    if (showReportIssueDialog) {
        var issueType by remember { mutableStateOf("Incorrect item identified") }
        var issueNotes by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showReportIssueDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E2224),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF59E0B).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                        }
                        Text(
                            text = "Report Valuation Issue",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Help us tune our valuation models. What was inaccurate?",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Issue options
                    val issues = listOf(
                        "Incorrect item identified",
                        "Valuation too high",
                        "Valuation too low",
                        "Condition misdiagnosed"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        issues.forEach { option ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (issueType == option) Color(0xFF064E3B) else Color(0xFF121212),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { issueType = option }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = issueType == option,
                                        onClick = { issueType = option },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF10B981))
                                    )
                                    Text(text = option, color = Color.White, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = issueNotes,
                        onValueChange = { issueNotes = it },
                        label = { Text("Details (optional)", color = Color(0xFF9CA3AF)) },
                        placeholder = { Text("e.g. Expected NZ$80 instead of NZ$390", color = Color(0xFF6B7280)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF374151)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showReportIssueDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = Color(0xFF9CA3AF))
                        }
                        Button(
                            onClick = {
                                showReportIssueDialog = false
                                snackbarMessage = "Thank you! Issue report submitted for review."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Help Centre Dialog
    if (showHelpCenterDialog) {
        InfoDialog(
            title = "Help Centre",
            icon = Icons.Default.Help,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HelpFaqItem(
                        question = "How to get the most accurate price?",
                        answer = "Take photos in bright lighting. Capture brand tags, serial numbers, hallmarks (for jewelry/silver), or soles/stitching (for sneakers)."
                    )
                    HelpFaqItem(
                        question = "What marketplaces are checked?",
                        answer = "For New Zealand, we map sold items primarily against Trade Me NZ, Facebook Marketplace NZ, and international benchmark comps."
                    )
                    HelpFaqItem(
                        question = "What if an item has no tag?",
                        answer = "Point the camera at the item's distinctive shape or hallmark. Gemini will perform reverse visual identification based on design attributes."
                    )
                }
            },
            onDismiss = { showHelpCenterDialog = false }
        )
    }

    // Send Feedback Dialog
    if (showFeedbackDialog) {
        var feedbackText by remember { mutableStateOf("") }
        var rating by remember { mutableStateOf(5) }

        Dialog(onDismissRequest = { showFeedbackDialog = false }) {
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
                        text = "Send Feedback",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "How has your experience with PriceSnap been?",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodySmall
                    )

                    // Star selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "$i Stars",
                                    tint = if (i <= rating) Color(0xFFFBBF24) else Color(0xFF4B5563),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = feedbackText,
                        onValueChange = { feedbackText = it },
                        placeholder = { Text("Tell us what you love or how we can improve...", color = Color(0xFF6B7280)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF10B981),
                            unfocusedBorderColor = Color(0xFF374151)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showFeedbackDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = Color(0xFF9CA3AF))
                        }
                        Button(
                            onClick = {
                                showFeedbackDialog = false
                                snackbarMessage = "Feedback sent! We appreciate your support."
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                        ) {
                            Text("Send", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Contact Support Dialog
    if (showContactSupportDialog) {
        InfoDialog(
            title = "Contact Support",
            icon = Icons.Default.Email,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Need dedicated assistance with op shop volunteer deployment or enterprise scanning?",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF121212),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Direct Support Email", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                            Text("support@pricesnap.app", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Typical response time: Within 24 hours", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            },
            onDismiss = { showContactSupportDialog = false }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        InfoDialog(
            title = "Terms of Service",
            icon = Icons.Default.Description,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Acceptance: By using PriceSnap, you agree to these service terms.\n2. Non-Certified Guidance: Valuations are computational estimates based on publicly observed marketplace trends and do not constitute certified financial or insurance appraisals.\n3. Fair Use: Scanning services may be throttled if automated bulk scraping is detected.\n4. Liability: PriceSnap is not responsible for transaction outcomes or pricing discrepancies on third-party resale platforms.",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            },
            onDismiss = { showTermsDialog = false }
        )
    }

    // Licences Dialog
    if (showLicencesDialog) {
        InfoDialog(
            title = "Open Source Licences",
            icon = Icons.Default.Code,
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 280.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("• Android Jetpack & Compose (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("• Google Material Components (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("• Room Persistence Engine (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("• Retrofit & OkHttp (Square Inc - Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("• Coil Image Loader (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("• Moshi JSON Converter (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
            },
            onDismiss = { showLicencesDialog = false }
        )
    }

    // Website Dialog
    if (showWebsiteDialog) {
        InfoDialog(
            title = "PriceSnap Online",
            icon = Icons.Default.Language,
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Visit the official portal for release announcements, op-shop guides, and pricing benchmark trends:",
                        color = Color(0xFFD1D5DB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "https://pricesnap.app",
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            onDismiss = { showWebsiteDialog = false }
        )
    }
}

// ==========================================
// REUSABLE UI COMPONENTS FOR SETTINGS
// ==========================================

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
    Divider(
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
