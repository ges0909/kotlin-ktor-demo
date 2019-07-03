package de.schrader.ktor.model.auth

data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String
)
