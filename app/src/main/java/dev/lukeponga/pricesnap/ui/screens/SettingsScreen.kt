package dev.lukeponga.pricesnap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Settings",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Install App Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2224))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Install App", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Add PriceSnap to your home screen for quick offline-ready scans.", color = Color(0xFF9CA3AF), style = MaterialTheme.typography.bodySmall)
            }
        }

        // About Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2224))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "About PriceSnap", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "PriceSnap is an AI-powered price scanner built for op shop staff and individuals to value thrift finds, resale items, clothing, sneakers, collectibles, and secondhand goods. Point your camera at an item to get new and used price ranges, sold-comp signals, and confidence notes in seconds.",
                    color = Color(0xFF9CA3AF),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Version 1.0.0", color = Color(0xFF6B7280), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
