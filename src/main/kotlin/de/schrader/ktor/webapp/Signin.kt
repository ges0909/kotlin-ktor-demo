package de.schrader.ktor.webapp

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

private const val SIGNIN = "/signin"

@KtorExperimentalLocationsAPI
@Location(SIGNIN)
data class Signin(val userId: String = "", val error: String = "")

@KtorExperimentalLocationsAPI
fun Route.signin(hashFunction: (String) -> String) {
    get<Signin> {
        call.respond(FreeMarkerContent("signin.ftl", null))
    }
}
