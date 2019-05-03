package de.schrader.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import org.slf4j.event.Level

const val jsonText = """{
    "id": 1,
    "task": "Pay water bill",
    "description": "Pay water bill today",
}"""

data class Person(val name: String = "Max", var age: Int = 30)

data class Todo(var id: Int, val name: String, val description: String, val completed: Boolean)

val todoList = ArrayList<Todo>()

fun Application.module() {

    log.info("Configuration ...")

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    install(CallLogging) {
        level = Level.INFO
        // Filter method keeps a whitelist of filters. If any of them returns true, the call is logged.
        // If no filters are defined, everything is logged.
        filter { call -> call.request.path().startsWith("/json") }
        filter { call -> call.request.path().startsWith("/person") }
        // filter { call -> call.request.path().startsWith("/todo") }
    }

    install(Routing) {
    }

    log.info("Routing ...")

    routing {
        route("/json") {
            // grouping routes
            get {
                call.respond(mapOf("task" to "Pay water bill", "description" to "Pay water bill today"))
            }
            get("/text") {
                call.respondText(jsonText, ContentType.Application.Json)
            }
        }
        get("/person") {
            val person = Person()
            call.respond(person)
        }
        route("/todo") {
            post {
                val todo = call.receive<Todo>()
                todo.id = todoList.size
                todoList.add(todo)
                call.respond("Added")

            }
            delete("/{id}") {
                call.respond(todoList.removeAt(call.parameters["id"]!!.toInt()))
            }
            get("/{id}") {
                call.respond(todoList[call.parameters["id"]!!.toInt()])
            }
            get {
                call.respond(todoList)
            }
        }
    }
}
