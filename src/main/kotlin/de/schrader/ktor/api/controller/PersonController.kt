package de.schrader.ktor.api.controller

import de.schrader.ktor.API_PREFIX
import de.schrader.ktor.api.model.Person
import de.schrader.ktor.api.service.PersonService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.koin.ktor.ext.inject

private const val PERSONS = "$API_PREFIX/persons"

fun Route.person() {

    val personService: PersonService by inject()

    route(PERSONS) {

        get {
            val persons = personService.findAll()
            call.respond(HttpStatusCode.OK, persons)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            personService.find(id)?.let { return@get call.respond(HttpStatusCode.OK, it) }
            call.respond(HttpStatusCode.NotFound)
        }

        post {
            val person = call.receive<Person>()
            personService.create(person)?.let { return@post call.respond(HttpStatusCode.Created, it) }
            call.respond(HttpStatusCode.InternalServerError)
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            when (personService.update(id, person)) {
                0 -> call.respond(HttpStatusCode.NotFound)
                1 -> call.respond(HttpStatusCode.OK)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            when (personService.delete(id)) {
                0 -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
