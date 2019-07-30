package de.schrader.ktor.webapp

import de.schrader.ktor.SIGNOUT_ROUTE
import de.schrader.ktor.auth.Session
import de.schrader.ktor.redirect
import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.routing.Route
import io.ktor.sessions.clear
import io.ktor.sessions.sessions

@Location(SIGNOUT_ROUTE)
class Signout

fun Route.signout() {
    get<Signout> {
        call.sessions.clear<Session>()
        call.redirect(Signin())
    }
}
