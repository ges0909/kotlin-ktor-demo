package de.schrader.ktor.webapp

import de.schrader.ktor.ROUTE_SIGNUP
import de.schrader.ktor.auth.MIN_PASSWORD_LENGTH
import de.schrader.ktor.auth.MIN_USER_ID_LENGTH
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.User
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.auth.isUserIdValid
import de.schrader.ktor.redirect
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.koin.ktor.ext.inject

@Location(ROUTE_SIGNUP)
data class Signup(
    val userId: String? = null,
    val displayName: String? = null,
    val email: String? = null,
    val error: String? = null
)

fun Route.signup(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()

    get<Signup> {
        call.sessions.get<Session>()?.let { session ->
            userRepository.findById(session.userId)?.let { return@get call.redirect(Home()) }
        }
        call.respond(FreeMarkerContent("signup.ftl", mapOf("error" to it.error)))
    }

    post<Signup> {
        val params = call.receive<Parameters>()

        val userId = params["userId"] ?: return@post call.redirect(Signup(error = "Missing user name"))
        val password = params["password"] ?: return@post call.redirect(Signup(error = "Missing password"))
        val displayName = params["displayName"] ?: return@post call.redirect(Signup(error = "Missing display name"))
        val email = params["email"] ?: return@post call.redirect(Signup(error = "Missing email"))

        val signup = Signup(userId, displayName, email)

        call.sessions.get<Session>()?.let { session ->
            userRepository.findById(session.userId)?.let {
                return@post call.redirect(signup.copy(error = "The user '${it.userId}' is already signed up"))
            }
        }

        when {
            userId.length < MIN_USER_ID_LENGTH ->
                return@post call.redirect(signup.copy(error = "User name should be at least $MIN_USER_ID_LENGTH chars long"))
            password.length < MIN_PASSWORD_LENGTH ->
                return@post call.redirect(signup.copy(error = "Password should be at least $MIN_PASSWORD_LENGTH chars long"))
            !isUserIdValid(userId) ->
                return@post call.redirect(signup.copy(error = "User name should consists of digits, chars, dots and underscores"))
            userRepository.findById(userId) != null ->
                return@post call.redirect(signup.copy(error = "User '$userId' is already registered"))
        }

        try {
            userRepository.create(User(userId, email, displayName, hashFunction(password)))
        } catch (e: Throwable) {
            application.log.error("Failed to register user '$userId'", e)
            return@post call.redirect(signup.copy(error = "Failed to register user '$userId'"))
        }
        call.redirect(Signin(userId = userId))
    }
}
