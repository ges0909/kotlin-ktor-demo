package de.schrader.ktor.webapp

import de.schrader.ktor.auth.Session
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.sessions.clear
import io.ktor.sessions.sessions

private const val SIGNOUT = "/signout"

@Location(SIGNOUT)
class Signout

fun Route.signout() {
    get<Signout> {
        call.sessions.clear<Session>()
        call.respondRedirect(application.locations.href(Signin()))
    }
}
