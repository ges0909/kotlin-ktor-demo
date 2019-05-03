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
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

const val jsonText = """{
    "id": 1,
    "task": "Pay water bill",
    "description": "Pay water bill today",
}"""

data class Person(val name: String = "Max", var age: Int = 30)

object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<Int>
    val name = varchar("name", 50) // Column<String>
    val age = integer("age") // Column<Int>
}

fun Application.module() {

    log.info("Connecting to database")

    // Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    Database.connect("jdbc:h2:~/test", driver = "org.h2.Driver")

    transaction {
        SchemaUtils.create(Persons)
//        val id = Persons.insert {
//            it[name] = "heike"
//            it[age] = 57
//        } get Persons.id
    }

    log.info("Configuring ktor")

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

    log.info("Start routing")

    routing {
        route("/json") {
            get {
                call.respond(mapOf("task" to "Pay water bill", "description" to "Pay water bill today"))
            }
            get("/text") {
                call.respondText(jsonText, ContentType.Application.Json)
            }
        }
        route("/person") {
            post {
                val person = call.receive<Person>()
                val id = transaction {
                    Persons.insert {
                        it[name] = person.name
                        it[age] = person.age
                    } get Persons.id
                }
                call.respond(id)
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
