package de.schrader.ktor.route

import de.schrader.ktor.service.PersonService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject

data class Person(val name: String, var age: Int)

fun Route.persons() {

    val personService: PersonService by inject()

    route("/persons") {

        get {
            call.respond(HttpStatusCode.OK, personService.get())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            call.respond(HttpStatusCode.OK, personService.getById(id))
        }

        post {
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.Created, personService.create(person))
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.OK, personService.update(id, person))
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            personService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
