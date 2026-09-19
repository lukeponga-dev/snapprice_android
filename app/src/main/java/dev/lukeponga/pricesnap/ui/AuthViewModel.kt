package dev.lukeponga.pricesnap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lukeponga.pricesnap.auth.FirebaseAuthenticationManager
import dev.lukeponga.pricesnap.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val authManager: FirebaseAuthenticationManager) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail: StateFlow<String?> = authManager.currentUserEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState

    fun continueAsGuest() {
        _isGuestMode.value = true
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authManager.login(email, password)) {
                is AuthResult.Success -> {
                    _isGuestMode.value = false
                    _authState.value = AuthUiState.Success
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authManager.signUp(email, password)) {
                is AuthResult.Success -> {
                    _isGuestMode.value = false
                    _authState.value = AuthUiState.Success
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isGuestMode.value = false
            authManager.logout()
        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }

    class Factory(private val authManager: FirebaseAuthenticationManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
