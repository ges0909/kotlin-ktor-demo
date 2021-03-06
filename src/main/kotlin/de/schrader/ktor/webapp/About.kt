package de.schrader.ktor.webapp

import de.schrader.ktor.ABOUT_ROUTE
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

@Location(ABOUT_ROUTE)
class About

fun Route.about() {
    val userRepository: UserRepository by inject()

    get<About> {
        val user = call.sessions.get<Session>()?.let { userRepository.findById(it.userId) }
        call.respond(FreeMarkerContent("about.ftl", mapOf("user" to user)))
    }
}
