package de.schrader.ktor.webapp

import de.schrader.ktor.Session
import de.schrader.ktor.repository.auth.UserRepository
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.koin.ktor.ext.inject

private const val SIGNUP = "/signup"

@Location(SIGNUP)
data class Signup(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

fun Route.signup(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()

    get<Signup> {
        call.respond(FreeMarkerContent("signup.ftl", null))
    }

    post<Signup> {
        call.sessions.get<Session>()?.let {
        }
    }
}
