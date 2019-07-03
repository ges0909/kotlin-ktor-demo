package de.schrader.ktor.auth

data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String
)
