package dev.lukeponga.pricesnap.auth

import com.google.firebase.auth.FirebaseAuth
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

    private val actualAuth: FirebaseAuth? by lazy {
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

    suspend fun login(email: String, password: String): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Authentication service unavailable. Please check your configuration.")
        return try {
            val result = authInstance.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(User(firebaseUser.email ?: ""))
            } else {
                AuthResult.Error("Login failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Authentication failed")
        }
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        val authInstance = actualAuth ?: return AuthResult.Error("Sign up service unavailable. Please check your configuration.")
        return try {
            val result = authInstance.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                AuthResult.Success(User(firebaseUser.email ?: ""))
            } else {
                AuthResult.Error("Sign up failed: User is null")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Sign up failed")
        }
    }

    suspend fun logout() {
        actualAuth?.signOut()
    }
}
