package de.schrader.ktor.webapp

import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get

private const val ABOUT_PATH = "/about"

fun Route.about() {
    get(ABOUT_PATH) {
        call.respond(FreeMarkerContent("about.ftl", null))
    }
}
