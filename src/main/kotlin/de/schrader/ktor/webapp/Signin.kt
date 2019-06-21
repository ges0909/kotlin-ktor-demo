package de.schrader.ktor.webapp

import de.schrader.ktor.repository.auth.UserRepository
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import org.koin.ktor.ext.inject

private const val SIGNIN = "/signin"

@Location(SIGNIN)
data class Signin(val userId: String = "", val error: String = "")

fun Route.signin(hashFunction: (String) -> String) {
    val userRepository: UserRepository by inject()

    get<Signin> {
        call.respond(FreeMarkerContent("signin.ftl", null))
    }
}
