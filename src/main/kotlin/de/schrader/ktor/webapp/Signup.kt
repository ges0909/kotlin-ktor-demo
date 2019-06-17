package de.schrader.ktor.webapp

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

private const val SIGNUP = "/signup"

@KtorExperimentalLocationsAPI
@Location(SIGNUP)
data class Signup(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

@KtorExperimentalLocationsAPI
fun Route.signup(hashFunction: (String) -> String) {
    get<Signup> {
        call.respond(FreeMarkerContent("signup.ftl", null))
    }
}
