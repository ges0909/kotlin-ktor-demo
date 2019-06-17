package de.schrader.ktor.model.auth

import java.io.Serializable

data class User(
    val userId: String,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable

