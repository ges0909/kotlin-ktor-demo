package de.schrader.ktor.controller

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
            val person = personService.get()
            call.respond(HttpStatusCode.OK, person)
//            when (val result = personService.get()) {
//                is Success -> {
//                    call.respond(HttpStatusCode.OK, result.value)
//                }
//                is Failure -> call.respond(HttpStatusCode.InternalServerError)
//            }
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = personService.getById(id)
            call.respond(HttpStatusCode.OK, person)
        }

        post {
            val person = call.receive<Person>()
            val person2 = personService.create(person)
            call.respond(HttpStatusCode.Created, person2)
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            personService.update(id, person)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            personService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
