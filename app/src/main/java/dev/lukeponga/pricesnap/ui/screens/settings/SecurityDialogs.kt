package dev.lukeponga.pricesnap.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SecurityDialog(
    userEmail: String?,
    onDismiss: () -> Unit,
    onSendReset: (String, (Result<Unit>) -> Unit) -> Unit,
    onUpdatePassword: (String, (Result<Unit>) -> Unit) -> Unit,
    onResult: (String) -> Unit
) {
    var newPasswordInput by remember { mutableStateOf("") }
    var isUpdating by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { if (!isUpdating) onDismiss() }) {
        Surface(shape = RoundedCornerShape(20.dp), color = Color(0xFF1E2224), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Security & Password", color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Manage credentials for ${userEmail ?: "your account"}.", color = Color(0xFF9CA3AF), fontSize = 13.sp)

                OutlinedButton(
                    onClick = {
                        if (!userEmail.isNullOrBlank()) {
                            onSendReset(userEmail) { result ->
                                onDismiss()
                                onResult(if (result.isSuccess) "Reset link sent to $userEmail" else result.exceptionOrNull()?.message ?: "Failed to send reset link")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF1E4336)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF34D399))
                ) {
                    Icon(Icons.Default.MailOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Password Reset Email", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                HorizontalDivider(color = Color(0xFF2D3748), modifier = Modifier.padding(vertical = 4.dp))

                Text("Or enter a new password (min. 6 chars):", color = Color(0xFFD1D5DB), fontSize = 13.sp)

                OutlinedTextField(
                    value = newPasswordInput,
                    onValueChange = { newPasswordInput = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF10B981),
                        unfocusedBorderColor = Color(0xFF2D3748),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, enabled = !isUpdating) { Text("Close", color = Color(0xFF9CA3AF)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newPasswordInput.length >= 6) {
                                isUpdating = true
                                onUpdatePassword(newPasswordInput) { result ->
                                    isUpdating = false
                                    onDismiss()
                                    onResult(if (result.isSuccess) "Password updated successfully!" else result.exceptionOrNull()?.message ?: "Failed to update password")
                                }
                            }
                        },
                        enabled = !isUpdating && newPasswordInput.length >= 6,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text(if (isUpdating) "Updating..." else "Update Password", color = Color(0xFF031612), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
