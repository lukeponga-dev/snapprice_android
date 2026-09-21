package dev.lukeponga.pricesnap.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import dev.lukeponga.pricesnap.BuildConfig
import dev.lukeponga.pricesnap.model.AuthResult
import dev.lukeponga.pricesnap.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Dedicated manager for Firebase Authentication.
 */
class FirebaseAuthenticationManager(private val auth: FirebaseAuth? = null) {

    val actualAuth: FirebaseAuth? by lazy {
        auth ?: try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }

    val isLoggedIn: Flow<Boolean> = callbackFlow {
        val authInstance = actualAuth
        if (authInstance == null) {
            trySend(false)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        authInstance.addAuthStateListener(listener)
        awaitClose { authInstance.removeAuthStateListener(listener) }
    }

    val currentUserEmail: Flow<String?> = callbackFlow {
        val authInstance = actualAuth
        if (authInstance == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.email)
        }
        authInstance.addAuthStateListener(listener)
        awaitClose { authInstance.removeAuthStateListener(listener) }
    }

    val currentUser: Flow<User?> = callbackFlow {
        val authInstance = actualAuth
        if (authInstance == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                trySend(
                    User(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = user.displayName,
                        isAnonymous = user.isAnonymous
                    )
                )
            } else {
                trySend(null)
            }
        }
        authInstance.addAuthStateListener(listener)
        awaitClose { authInstance.removeAuthStateListener(listener) }
    }

    fun getCurrentUid(): String? = actualAuth?.currentUser?.uid

    fun getCurrentUser(): User? {
        val u = actualAuth?.currentUser ?: return null
        return User(
            uid = u.uid,
            email = u.email ?: "",
            displayName = u.displayName,
            isAnonymous = u.isAnonymous
        )
    }

    suspend fun login(email: String, password: String): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Firebase Authentication service unavailable.")
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }
        return try {
            val result = authInstance.signInWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName,
                        isAnonymous = firebaseUser.isAnonymous
                    )
                )
            } else {
                AuthResult.Error("Login failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Authentication failed")
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String? = null): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Firebase Authentication service unavailable.")
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Email and password cannot be empty.")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters.")
        }
        return try {
            val result = authInstance.createUserWithEmailAndPassword(email.trim(), password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                if (!displayName.isNullOrBlank()) {
                    try {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                        firebaseUser.updateProfile(profileUpdates).await()
                    } catch (ignore: Exception) { }
                }
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = displayName?.trim() ?: firebaseUser.displayName,
                        isAnonymous = firebaseUser.isAnonymous
                    )
                )
            } else {
                AuthResult.Error("Sign up failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Sign up failed")
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val authInstance = actualAuth ?: return Result.failure(Exception("Firebase Authentication is not configured."))
        if (email.isBlank()) {
            return Result.failure(Exception("Please enter your email address."))
        }
        return try {
            authInstance.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Firebase Authentication is not configured.")
        return try {
            val result = authInstance.signInAnonymously().await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(
                    User(
                        uid = firebaseUser.uid,
                        email = "Guest User",
                        isAnonymous = true
                    )
                )
            } else {
                AuthResult.Error("Guest login failed.")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Guest login failed")
        }
    }

    suspend fun signInWithGoogle(context: Context): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Firebase Authentication is not configured.")
        val credentialManager = CredentialManager.create(context)

        // Web Client ID is required for Google Sign-In on Android with Firebase.
        // Falls back to the project's OAuth client ID if not configured in secrets.
        val webClientId = try {
            val id = BuildConfig.GOOGLE_WEB_CLIENT_ID
            if (id.isNullOrBlank() || id.contains("your-google-web-client-id")) {
                "805381652466-2otcnb943n58i5cf3oqfbgsvf9o8gol9.apps.googleusercontent.com"
            } else {
                id
            }
        } catch (e: Exception) {
            "805381652466-2otcnb943n58i5cf3oqfbgsvf9o8gol9.apps.googleusercontent.com"
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                val authResult = authInstance.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    AuthResult.Success(
                        User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            displayName = firebaseUser.displayName,
                            isAnonymous = firebaseUser.isAnonymous
                        )
                    )
                } else {
                    AuthResult.Error("Google Sign-In failed: User is null")
                }
            } else {
                AuthResult.Error("Unexpected credential type: ${credential.type}")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Google Sign-In failed")
        }
    }

    suspend fun logout() {
        actualAuth?.signOut()
    }
}

