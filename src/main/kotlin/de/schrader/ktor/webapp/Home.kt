package de.schrader.ktor.webapp

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

private const val HOME = "/"

@Location(HOME)
class Home

fun Route.home() {
    get<Home> {
        call.respond(FreeMarkerContent("home.ftl", null))
    }
}
