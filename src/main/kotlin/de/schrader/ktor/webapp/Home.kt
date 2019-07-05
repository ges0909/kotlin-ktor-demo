package de.schrader.ktor.webapp

import de.schrader.ktor.ROUTE_HOME
import de.schrader.ktor.auth.Session
import de.schrader.ktor.auth.UserRepository
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import org.koin.ktor.ext.inject

@Location(ROUTE_HOME)
class Home

fun Route.home() {
    val userRepository: UserRepository by inject()

    get<Home> {
        val user = call.sessions.get<Session>()?.let { userRepository.findById(it.userId) }
        call.respond(FreeMarkerContent("home.ftl", mapOf("user" to user)))
    }
}
