package dev.lukeponga.pricesnap.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String? = null,
    val isAnonymous: Boolean = false
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
