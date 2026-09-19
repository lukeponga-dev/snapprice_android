package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.R
import dev.lukeponga.pricesnap.ui.AuthUiState
import dev.lukeponga.pricesnap.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val authState by viewModel.authState.collectAsState()
    var isLogin by remember { mutableStateOf(true) }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Decorative background gradients
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF10B981).copy(alpha = 0.15f), Color.Transparent),
                        center = androidx.compose.ui.geometry.Offset(0f, 0f),
                        radius = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo and Branding
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF131E1B))
                    .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E332C)), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFB923C))
                        .align(Alignment.TopEnd)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PriceSnap",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = if (isLogin) "Welcome back" else "Create an account",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Input Fields
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0xFF1E332C),
                    focusedLabelColor = Color(0xFF10B981),
                    unfocusedLabelColor = Color(0xFF9CA3AF),
                    cursorColor = Color(0xFF10B981)
                ),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF62A894)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF10B981),
                    unfocusedBorderColor = Color(0xFF1E332C),
                    focusedLabelColor = Color(0xFF10B981),
                    unfocusedLabelColor = Color(0xFF9CA3AF),
                    cursorColor = Color(0xFF10B981)
                ),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF62A894)) },
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null, tint = Color(0xFF62A894))
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = {
                    if (isLogin) viewModel.login(email, password)
                    else viewModel.signUp(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color(0xFF031612)
                ),
                enabled = authState !is AuthUiState.Loading
            ) {
                if (authState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFF031612),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isLogin) "Sign In" else "Sign Up",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Guest mode
            OutlinedButton(
                onClick = { viewModel.continueAsGuest() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF34D399)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E332C))
            ) {
                Text(
                    text = "Continue as Guest",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle Login/Signup
            TextButton(
                onClick = { 
                    isLogin = !isLogin 
                    viewModel.resetState()
                }
            ) {
                Text(
                    text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Sign In",
                    color = Color(0xFF34D399)
                )
            }

            // Error Display
            AnimatedVisibility(visible = authState is AuthUiState.Error) {
                val error = authState as? AuthUiState.Error
                Text(
                    text = error?.message ?: "An error occurred",
                    color = Color(0xFFF87171),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Legal disclaimer at bottom
        Text(
            text = "By continuing, you agree to our Terms of Service\nand Privacy Policy.",
            color = Color(0xFF4B5563),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .navigationBarsPadding()
        )
    }
}
