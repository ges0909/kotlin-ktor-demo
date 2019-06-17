package de.schrader.ktor.webapp

import io.ktor.application.call
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respondRedirect
import io.ktor.routing.Route

private const val SIGNOUT = "/signout"

@KtorExperimentalLocationsAPI
@Location(SIGNOUT)
class Signout

@KtorExperimentalLocationsAPI
fun Route.signout(hashFunction: (String) -> String) {
    get<Signout> {
        call.respondRedirect { Signin() }
    }
}
