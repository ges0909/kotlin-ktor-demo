package de.schrader.ktor.controller

import de.schrader.ktor.None
import de.schrader.ktor.Person
import de.schrader.ktor.Some
import de.schrader.ktor.service.PersonService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.persons() {

    val personService: PersonService by inject()

    route("/persons") {

        get {
            val person = personService.all()
            call.respond(HttpStatusCode.OK, person)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            when (val thing = personService.read(id)) {
                is Some -> call.respond(HttpStatusCode.OK, thing.value)
                is None -> call.respond(HttpStatusCode.NotFound)
            }
        }

        post {
            val person = call.receive<Person>()
            when (val thing = personService.create(person)) {
                is Some -> call.respond(HttpStatusCode.Created, thing.value)
                is None -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            personService.update(id, person)
            call.respond(HttpStatusCode.OK)
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
