package dev.lukeponga.pricesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.lukeponga.pricesnap.ui.AppraisalUiState
import dev.lukeponga.pricesnap.ui.AppraisalViewModel
import dev.lukeponga.pricesnap.ui.AuthViewModel
import dev.lukeponga.pricesnap.ui.screens.*

class MainActivity : ComponentActivity() {
    private val viewModel: AppraisalViewModel by viewModels {
        val app = application as PriceSnapApp
        AppraisalViewModel.Factory(app.repository, app.scanPreferenceManager)
    }
    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModel.Factory((application as PriceSnapApp).authManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PriceSnapAppTheme {
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val isGuestMode by authViewModel.isGuestMode.collectAsState()
                
                if (isLoggedIn || isGuestMode) {
                    MainContainer(viewModel = viewModel, authViewModel = authViewModel)
                } else {
                    AuthScreen(viewModel = authViewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: AppraisalViewModel, authViewModel: AuthViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val uiState by viewModel.uiState.collectAsState()
    val historyList by viewModel.history.collectAsState()
    val backendStatus by viewModel.backendStatus.collectAsState()
    val configuration = LocalConfiguration.current
    val compactWidth = configuration.screenWidthDp < 360
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        if (uiState is AppraisalUiState.Error) {
            snackbarHostState.showSnackbar((uiState as AppraisalUiState.Error).message)
            viewModel.resetState()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = if (compactWidth) 10.dp else 16.dp)
                            .size(if (compactWidth) 36.dp else 40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF131E1B))
                            .border(androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E332C)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PhotoCamera, null, tint = Color.White, modifier = Modifier.size(20.dp))
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
                    val (statusColor, dotColor, label) = when (backendStatus) {
                        is dev.lukeponga.pricesnap.ui.BackendStatus.Connected -> Triple(Color(0xFF34D399), Color(0xFF10B981), "Online")
                        is dev.lukeponga.pricesnap.ui.BackendStatus.Checking -> Triple(Color(0xFFFBBF24), Color(0xFFF59E0B), "Checking")
                        is dev.lukeponga.pricesnap.ui.BackendStatus.Offline -> Triple(Color(0xFFF87171), Color(0xFFEF4444), "Offline")
                        is dev.lukeponga.pricesnap.ui.BackendStatus.Error -> Triple(Color(0xFFF87171), Color(0xFFEF4444), "Degraded")
                    }
                    Surface(
                        color = Color(0xFF07261E),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0E4336)),
                        modifier = Modifier.padding(end = if (compactWidth) 10.dp else 16.dp).height(28.dp).clickable { viewModel.checkBackendHealth() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = if (compactWidth) 9.dp else 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(Modifier.size(6.dp).clip(CircleShape).background(dotColor))
                            if (!compactWidth) Text(label, fontSize = 12.sp, color = statusColor, fontWeight = FontWeight.Medium)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF031612),
                tonalElevation = 0.dp,
                windowInsets = NavigationBarDefaults.windowInsets,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 1.dp, color = Color(0xFF07271F))
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
                        onClick = {
                            viewModel.resetState()
                            selectedTab = index
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = contentDescription,
                                modifier = Modifier.size(if (compactWidth) 22.dp else 24.dp)
                            )
                        },
                        label = { Text(label, fontSize = if (compactWidth) 10.sp else 12.sp, maxLines = 1) },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF34D399),
                            selectedTextColor = Color(0xFF34D399),
                            unselectedIconColor = Color(0xFF62A894),
                            unselectedTextColor = Color(0xFF62A894),
                            indicatorColor = Color(0xFF0E3D31)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            color = Color.Black
        ) {
            when {
                uiState is AppraisalUiState.Success -> ResultScreen(
                    appraisal = (uiState as AppraisalUiState.Success).appraisal,
                    onScanAgain = {
                        viewModel.resetState()
                        selectedTab = 1
                    },
                    onSaveResult = {
                        viewModel.resetState()
                        selectedTab = 2
                    }
                )
                uiState is AppraisalUiState.Loading -> ScanningScreen(backendStatus = backendStatus)
                else -> when (selectedTab) {
                    0 -> HomeScreen(totalScans = historyList.size, onNavigateToScan = { selectedTab = 1 })
                    1 -> {
                        val isGuest by authViewModel.isGuestMode.collectAsState()
                        ScanScreen(viewModel = viewModel, isGuest = isGuest, onScanCompleted = {})
                    }
                    2 -> {
                        val syncStatus by viewModel.syncStatus.collectAsState()
                        HistoryScreen(
                            hasScans = historyList.isNotEmpty(),
                            historyItems = historyList,
                            syncStatus = syncStatus,
                            onDeleteItem = { item -> viewModel.deleteHistoryItem(item) },
                            onStartScanning = { selectedTab = 1 }
                        )
                    }
                    3 -> {
                        val userEmail by authViewModel.userEmail.collectAsState()
                        val isGuest by authViewModel.isGuestMode.collectAsState()
                        val scanCount by viewModel.dailyScanCount.collectAsState()
                        
                        SettingsScreen(
                            onClearHistory = { viewModel.clearHistory() },
                            onLogout = { authViewModel.logout() },
                            userEmail = userEmail,
                            isGuest = isGuest,
                            scanCount = scanCount
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
