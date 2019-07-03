package de.schrader.ktor.webapp

import de.schrader.ktor.MIN_PASSWORD_LENGTH
import de.schrader.ktor.MIN_USER_ID_LENGTH
import de.schrader.ktor.Session
import de.schrader.ktor.isUserIdValid
import de.schrader.ktor.model.auth.User
import de.schrader.ktor.repository.auth.UserRepository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
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
    val error: String? = null
)

fun Route.signup(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()

    get<Signup> {
        call.sessions.get<Session>()?.let { session ->
            userRepository.findById(session.userId)?.let {
                call.respondRedirect(application.locations.href(Home()))
            }
        }
        call.respond(FreeMarkerContent("signup.ftl", mapOf("error" to it.error)))
    }

    post<Signup> {
        val params = call.receive<Parameters>()

        val userId = params["userId"]
            ?: return@post call.respondRedirect(application.locations.href(Signup(error = "Missing user id")))
        val password = params["password"]
            ?: return@post call.respondRedirect(application.locations.href(Signup(error = "Missing password")))
        val displayName = params["displayName"]
            ?: return@post call.respondRedirect(application.locations.href(Signup(error = "Missing display name")))
        val email = params["email"]
            ?: return@post call.respondRedirect(application.locations.href(Signup(error = "Missing email")))

        val signup = Signup(userId, displayName, email)

        call.sessions.get<Session>()?.let { session ->
            userRepository.findById(session.userId)?.let {
                return@post call.respondRedirect(
                    application.locations.href(signup.copy(error = "The user '${it.userId}' is already signed up"))
                )
            }
        }

        when {
            userId.length < MIN_USER_ID_LENGTH -> return@post call.respondRedirect(
                application.locations.href(signup.copy(error = "User id should be at least $MIN_USER_ID_LENGTH chars long"))
            )
            password.length < MIN_PASSWORD_LENGTH -> return@post call.respondRedirect(
                application.locations.href(signup.copy(error = "Password should be at least $MIN_PASSWORD_LENGTH chars long"))
            )
            !isUserIdValid(userId) -> return@post call.respondRedirect(
                application.locations.href(signup.copy(error = "User id should consists of digits, chars, dots and underscores"))
            )
            userRepository.findById(userId) != null -> return@post call.respondRedirect(
                application.locations.href(signup.copy(error = "User '$userId' is already registered"))
            )
        }

        try {
            userRepository.create(User(userId, email, displayName, hashFunction(password)))
        } catch (e: Throwable) {
            application.log.error("Failed to register user '$userId'", e)
            return@post call.respondRedirect(
                application.locations.href(signup.copy(error = "Failed to register user '$userId'"))
            )
        }
        call.respondRedirect(application.locations.href(Signin(userId = userId)))
    }
}
