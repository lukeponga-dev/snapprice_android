package dev.lukeponga.pricesnap.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.lukeponga.pricesnap.ui.screens.LegalPolicies

@Composable
fun PhotoRetentionDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "Photo Retention Explanation",
        icon = Icons.Default.Security,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("How PriceSnap handles your photos:", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "• Photos taken with PriceSnap are transmitted securely over TLS/SSL directly to the vision appraisal endpoint.\n• Images are processed in ephemeral memory strictly for visual extraction (brand labels, materials, conditions, serials).\n• Photos are NEVER retained on remote servers, sold to data brokers, or used to build facial or biometric profiles.\n• Local thumbnail previews are retained strictly on your own device until you delete them.",
                    color = Color(0xFFD1D5DB),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun AiDisclosureDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "AI Processing Disclosure",
        icon = Icons.Default.AutoAwesome,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Automated Valuation Modeling", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "PriceSnap uses multimodal Gemini computer vision models to identify items, read wear and tear, and benchmark listings against secondhand marketplaces including Trade Me, eBay, and Marketplace.\n\nAppraisals represent automated statistical market estimates, not formal accredited insurance appraisals. Always verify valuable jewelry, fine art, or high-end collectibles with a certified valuer.",
                    color = Color(0xFFD1D5DB),
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun ReportIssueDialog(onDismiss: () -> Unit, onSubmitted: (String) -> Unit) {
    var issueType by remember { mutableStateOf("Incorrect item identified") }
    var issueNotes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF1E2224), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF59E0B).copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.ReportProblem, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                    }
                    Text("Report Valuation Issue", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Text("Help us tune our valuation models. What was inaccurate?", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodySmall)

                val issues = listOf("Incorrect item identified", "Valuation too high", "Valuation too low", "Condition misdiagnosed")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    issues.forEach { option ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (issueType == option) Color(0xFF064E3B) else Color(0xFF121212),
                            modifier = Modifier.fillMaxWidth().clickable { issueType = option }
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                RadioButton(selected = issueType == option, onClick = { issueType = option }, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF10B981)))
                                Text(text = option, color = Color.White, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = issueNotes,
                    onValueChange = { issueNotes = it },
                    label = { Text("Details (optional)", color = Color(0xFF9CA3AF)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = Color(0xFF10B981), unfocusedBorderColor = Color(0xFF374151)),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) { Text("Cancel", color = Color(0xFF9CA3AF)) }
                    Button(onClick = { onSubmitted("Issue report submitted.") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                        Text("Submit", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun HelpCenterDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "Help Centre",
        icon = Icons.Default.Help,
        content = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HelpFaqItem("How accurate are the valuations?", "PriceSnap uses computational estimates based on active and sold listings. They are indicative market benchmarks, not official certified appraisals.")
                HelpFaqItem("Why can't I see my history?", "History is stored locally or synced via Firebase. Ensure you're signed in to access scans from other devices.")
                HelpFaqItem("Is my data safe?", "Photos are processed ephemerally and never sold. Your history is stored on your device and your private cloud account.")
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun ContactSupportDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "Contact Support",
        icon = Icons.Default.Email,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Need dedicated assistance with op shop volunteer deployment or enterprise scanning?", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodyMedium)
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFF121212), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Direct Support Email", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.labelSmall)
                        Text("support@pricesnap.app", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun TermsDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "Terms of Service",
        icon = Icons.Default.Description,
        content = {
            Text("1. Acceptance: By using PriceSnap, you agree to these service terms.\n2. Non-Certified Guidance: Valuations are computational estimates and do not constitute certified financial or insurance appraisals.\n3. Fair Use: Scanning services may be throttled if automated bulk scraping is detected.", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp, modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState()))
        },
        onDismiss = onDismiss
    )
}

@Composable
fun LicencesDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "Open Source Licences",
        icon = Icons.Default.Code,
        content = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("• Android Jetpack & Compose (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                Text("• Google Material Components (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                Text("• Room Persistence Engine (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
                Text("• Retrofit & OkHttp (Apache 2.0)", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
        },
        onDismiss = onDismiss
    )
}

@Composable
fun WebsiteDialog(onDismiss: () -> Unit) {
    InfoDialog(
        title = "PriceSnap Online",
        icon = Icons.Default.Language,
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Visit the official portal for release announcements and pricing benchmark trends:", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodyMedium)
                Text("https://pricesnap.app", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        onDismiss = onDismiss
    )
}
