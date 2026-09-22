package dev.lukeponga.pricesnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import dev.lukeponga.pricesnap.ui.AppraisalViewModel
import dev.lukeponga.pricesnap.ui.AuthViewModel
import dev.lukeponga.pricesnap.ui.MainContainer
import dev.lukeponga.pricesnap.ui.screens.AuthScreen
import dev.lukeponga.pricesnap.ui.theme.PriceSnapTheme

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
            PriceSnapTheme {
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
