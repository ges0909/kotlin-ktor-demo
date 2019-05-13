package de.schrader.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

data class Person(val name: String = "Max", var age: Int = 30)

fun Application.features() {
    // log.info("Install features")
    install(StatusPages)
    install(DefaultHeaders)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(CallLogging) {
        level = Level.INFO
        // if filter returns true, the call is logged; if no filters are defined, everything is logged
        filter { call -> call.request.path().startsWith("/person") }
    }
}

fun Application.module() {
    features()
    Repository.init()
    routing {
        route("/person") {
            post {
                val person = call.receive<Person>()
                val id = transaction {
                    Persons.insert {
                        it[name] = person.name
                        it[age] = person.age
                    } get Persons.id
                }
                val result = transaction {
                    val query = Persons.select { Persons.id eq id }
                    query.first()
                }
                call.respond(HttpStatusCode.Created)
            }
            delete("/{id}") {
                //call.respond(todoList.removeAt(call.parameters["id"]!!.toInt()))
            }
            get("/{id}") {
                //call.respond(todoList[call.parameters["id"]!!.toInt()])
            }
            get {
                //call.respond(todoList)
            }
        }
    }
}
