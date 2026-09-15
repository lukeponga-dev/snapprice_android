package dev.lukeponga.pricesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.ui.AppraisalUiState
import dev.lukeponga.pricesnap.ui.AppraisalViewModel
import dev.lukeponga.pricesnap.ui.screens.*

class MainActivity : ComponentActivity() {
    private val viewModel: AppraisalViewModel by viewModels {
        AppraisalViewModel.Factory((application as PriceSnapApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PriceSnapAppTheme {
                MainContainer(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: AppraisalViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = when (selectedTab) {
                                0 -> "Home"
                                1 -> "Scan"
                                2 -> "History"
                                3 -> "Settings"
                                else -> ""
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    // App Logo with orange dot
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2224)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF97316))
                                .align(Alignment.TopEnd)
                                .offset(x = 1.dp, y = (-1).dp)
                        )
                    }
                },
                actions = {
                    // Online status pill
                    Surface(
                        color = Color(0xFF1E2224),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(24.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "Online",
                                fontSize = 10.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF121212),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF181A1B),
                tonalElevation = 0.dp,
                modifier = Modifier.height(72.dp)
            ) {
                val items = listOf(
                    Triple("Home", Icons.Outlined.Home, Icons.Filled.Home),
                    Triple("Scan", Icons.Outlined.PhotoCamera, Icons.Filled.PhotoCamera),
                    Triple("History", Icons.Outlined.History, Icons.Filled.History),
                    Triple("Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
                )

                items.forEachIndexed { index, (label, icon, selectedIcon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) selectedIcon else icon,
                                contentDescription = label
                            )
                        },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF10B981),
                            selectedTextColor = Color(0xFF10B981),
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF121212)
        ) {
            when {
                // If model returned a success appraisal result, override view to ResultScreen
                uiState is AppraisalUiState.Success -> {
                    ResultScreen(
                        appraisal = (uiState as AppraisalUiState.Success).appraisal,
                        onScanAgain = { viewModel.resetState() },
                        onSaveResult = { viewModel.resetState() }
                    )
                }
                // Loading / Analyzing state
                uiState is AppraisalUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF10B981))
                    }
                }
                // Otherwise navigate tabs normally
                else -> {
                    when (selectedTab) {
                        0 -> HomeScreen(
                            totalScans = 0,
                            onNavigateToScan = { selectedTab = 1 }
                        )
                        1 -> ScanScreen(
                            viewModel = viewModel,
                            onScanCompleted = {}
                        )
                        2 -> HistoryScreen(
                            hasScans = false,
                            onStartScanning = { selectedTab = 1 }
                        )
                        3 -> SettingsScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun PriceSnapAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF121212),
            surface = Color(0xFF1E2224),
            primary = Color(0xFF10B981)
        ),
        content = content
    )
}
