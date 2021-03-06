package de.schrader.ktor.webapp

import de.schrader.ktor.PERSONS_ENDPOINT
import de.schrader.ktor.SIGNIN_ROUTE
import de.schrader.ktor.auth.MIN_PASSWORD_LENGTH
import de.schrader.ktor.auth.MIN_USER_ID_LENGTH
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.auth.isUserIdValid
import de.schrader.ktor.redirect
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.koin.ktor.ext.inject

@Location(SIGNIN_ROUTE)
data class Signin(val userId: String? = null, var error: String? = null)

fun Route.signin(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()

    get<Signin> {
        call.sessions.get<Session>()?.let { session ->
            userRepository.findById(session.userId)?.let {
                return@get call.respondRedirect(PERSONS_ENDPOINT)
            }
        }
        call.respond(FreeMarkerContent("signin.ftl", mapOf("userId" to it.userId, "error" to it.error)))
    }

    post<Signin> {
        val params = call.receive<Parameters>()

        val userId = params["userId"]
            ?: return@post call.redirect(Signin(error = "Missing user name"))
        val password = params["password"]
            ?: return@post call.redirect(Signin(error = "Missing password"))

        val signin = Signin(userId = userId)

        when {
            userId.length < MIN_USER_ID_LENGTH ->
                return@post call.redirect(signin.copy(error = "User name should be at least $MIN_USER_ID_LENGTH chars long"))
            password.length < MIN_PASSWORD_LENGTH ->
                return@post call.redirect(signin.copy(error = "Password should be at least $MIN_PASSWORD_LENGTH chars long"))
            !isUserIdValid(userId) ->
                return@post call.redirect(signin.copy(error = "User name should consists of digits, chars, dots and underscores"))
            userRepository.findByIdAndHash(userId, hashFunction(password)) == null ->
                return@post call.redirect(signin.copy(error = "Invalid user name or password"))
        }

        call.sessions.set(Session(userId))
        call.respondRedirect(PERSONS_ENDPOINT)
    }
}
