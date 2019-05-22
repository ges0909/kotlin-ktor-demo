package de.schrader.ktor.controller

import arrow.core.getOrElse
import de.schrader.ktor.None
import de.schrader.ktor.Some
import de.schrader.ktor.repository.Person
import de.schrader.ktor.service.PersonService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject

@KtorExperimentalLocationsAPI
fun Route.persons() {

    val personService: PersonService by inject()

    route("/persons") {

        get {
            call.respond(HttpStatusCode.OK, personService.all().getOrElse { "" })
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            when (val option = personService.read(id)) {
                is Some<*> -> call.respond(HttpStatusCode.OK, option.getOrElse { "" })
                is None<*> -> call.respond(HttpStatusCode.NotFound)
            }
        }

        post {
            val person = call.receive<Person>()
            when (val option = personService.create(person)) {
                is Some<*> -> {
                    // val path = locations.href(option.getOrElse { "" })
                    call.respond(HttpStatusCode.Created, option.getOrElse { "" })
                }
                is None<*> -> call.respond(HttpStatusCode.InternalServerError)
            }
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
