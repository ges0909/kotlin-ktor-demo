package de.schrader.ktor.webapp

import de.schrader.ktor.repository.auth.UserRepository
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import org.koin.ktor.ext.inject

private const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class Signout

fun Route.signout() {
    val userRepository: UserRepository by inject()

    get<Signout> {
        call.respondRedirect { Signin() }
    }
}
