package de.schrader.ktor.api.model

import de.schrader.ktor.JwtService
import de.schrader.ktor.LOGIN_ENDPOINT
import de.schrader.ktor.auth.UserRepository
import de.schrader.ktor.auth.hash
import de.schrader.ktor.redirect
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respondText
import io.ktor.routing.Route
import org.koin.ktor.ext.inject

@Location(LOGIN_ENDPOINT)
class Login

fun Route.login() {
    val jwtService: JwtService by inject()
    val userRepository: UserRepository by inject()

    post<Login> {
        val params = call.receive<Parameters>()

        val userId = params["userId"] ?: return@post call.redirect(it)
        val password = params["password"] ?: return@post call.redirect(it)

        userRepository.findByIdAndHash(userId, hash(password))?.let { user ->
            val token = jwtService.generateToken(user)
            return@post call.respondText(token)
        }
        call.respondText("Invalid user")
    }
}
