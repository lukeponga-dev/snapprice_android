package dev.lukeponga.pricesnap.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager for Supabase Authentication.
 * Placeholder implementation as requested in the architecture.
 */
class SupabaseAuthManager {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    suspend fun login() {
        // Implement Supabase Login logic here
        _isLoggedIn.value = true
    }

    suspend fun logout() {
        // Implement Supabase Logout logic here
        _isLoggedIn.value = false
    }
}
