package de.schrader.ktor.auth

import io.ktor.auth.Principal

data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Principal
