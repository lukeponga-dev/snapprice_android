package dev.lukeponga.pricesnap.model

data class User(
    val email: String,
    val name: String? = null
)

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
