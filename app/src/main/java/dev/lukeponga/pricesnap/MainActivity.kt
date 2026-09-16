package dev.lukeponga.pricesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.res.painterResource
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
    val historyList by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = when (selectedTab) {
                                0 -> "Home"
                                1 -> "Scan item"
                                2 -> "History"
                                3 -> "Settings"
                                else -> ""
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                },
                navigationIcon = {
                    // App Logo with orange dot
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131E1B))
                            .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E332C)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFB923C))
                                .align(Alignment.TopEnd)
                        )
                    }
                },
                actions = {
                    // Online status pill
                    Surface(
                        color = Color(0xFF07261E),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0E4336)),
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .height(28.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Text(
                                text = "Online",
                                fontSize = 12.sp,
                                color = Color(0xFF34D399),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF000000),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF031612),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF07271F))
            ) {
                NavigationBar(
                    containerColor = Color(0xFF031612),
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(72.dp)
                ) {
                    val items = listOf(
                        Triple("Home", R.drawable.ic_home, "Home"),
                        Triple("Scan", R.drawable.ic_scan, "Scan"),
                        Triple("History", R.drawable.ic_history, "History"),
                        Triple("Settings", R.drawable.ic_settings, "Settings")
                    )

                    items.forEachIndexed { index, (label, iconRes, contentDescription) ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = contentDescription,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF34D399),
                                selectedTextColor = Color(0xFF34D399),
                                unselectedIconColor = Color(0xFF62A894),
                                unselectedTextColor = Color(0xFF62A894),
                                indicatorColor = Color(0xFF223354)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF000000)
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
                            totalScans = historyList.size,
                            onNavigateToScan = { selectedTab = 1 }
                        )
                        1 -> ScanScreen(
                            viewModel = viewModel,
                            onScanCompleted = {}
                        )
                        2 -> HistoryScreen(
                            hasScans = historyList.isNotEmpty(),
                            historyItems = historyList,
                            onStartScanning = { selectedTab = 1 }
                        )
                        3 -> SettingsScreen(
                            onClearHistory = { viewModel.clearHistory() }
                        )
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
            background = Color(0xFF000000),
            surface = Color(0xFF0C1D19),
            primary = Color(0xFF10B981),
            secondary = Color(0xFF34D399)
        ),
        content = content
    )
}
