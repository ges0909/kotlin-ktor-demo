package de.schrader.ktor.model.auth

import io.ktor.auth.Principal

data class User(val displayName: String) : Principal
