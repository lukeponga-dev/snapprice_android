package dev.lukeponga.pricesnap.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.lukeponga.pricesnap.auth.FirebaseAuthenticationManager
import dev.lukeponga.pricesnap.model.AuthResult
import dev.lukeponga.pricesnap.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val authManager: FirebaseAuthenticationManager) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userEmail: StateFlow<String?> = authManager.currentUserEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentUser: StateFlow<User?> = authManager.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isGuestMode = MutableStateFlow(false)
    val isGuestMode: StateFlow<Boolean> = _isGuestMode

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState

    private val _resetPasswordState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val resetPasswordState: StateFlow<PasswordResetState> = _resetPasswordState.asStateFlow()

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

    fun signInWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authManager.signInWithGoogle(context)) {
                is AuthResult.Success -> {
                    _isGuestMode.value = false
                    _authState.value = AuthUiState.Success
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String? = null) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            when (val result = authManager.signUp(email, password, displayName)) {
                is AuthResult.Success -> {
                    _isGuestMode.value = false
                    _authState.value = AuthUiState.Success
                }
                is AuthResult.Error -> _authState.value = AuthUiState.Error(result.message)
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = PasswordResetState.Loading
            val result = authManager.sendPasswordReset(email)
            if (result.isSuccess) {
                _resetPasswordState.value = PasswordResetState.Success("Password reset email sent! Check your inbox.")
            } else {
                _resetPasswordState.value = PasswordResetState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "Failed to send reset email."
                )
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.sendPasswordReset(email)
            onResult(result)
        }
    }

    fun resetPasswordState() {
        _resetPasswordState.value = PasswordResetState.Idle
    }

    fun updateDisplayName(name: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.updateDisplayName(name)
            onResult(result)
        }
    }

    fun updatePassword(newPassword: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.updatePassword(newPassword)
            onResult(result)
        }
    }

    fun deleteAccount(onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = authManager.deleteAccount()
            if (result.isSuccess) {
                _isGuestMode.value = false
            }
            onResult(result)
        }
    }

    fun exitGuestMode() {
        _isGuestMode.value = false
    }

    fun logout(context: Context? = null) {
        viewModelScope.launch {
            _isGuestMode.value = false
            authManager.logout(context)
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

