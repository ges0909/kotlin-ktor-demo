package de.schrader.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.Level

private data class Person(val name: String = "Max", var age: Int = 30)

private object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey() // Column<Int>
    val name = varchar("name", 50) // Column<String>
    val age = integer("age") // Column<Int>
}

fun Application.module() {
    log.info("Start application ...")

    install(DefaultHeaders)
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

    Database.connect(url = "jdbc:h2:~/test;DATABASE_TO_UPPER=false", driver = "org.h2.Driver")
    transaction {
        SchemaUtils.create(Persons)
    }

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
                    Persons.select { Persons.id eq id }.first()
                }
                call.respond(result)
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
