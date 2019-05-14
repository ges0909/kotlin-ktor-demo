package de.schrader.ktor.route

import de.schrader.ktor.domain.Persons
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

private data class Person(val name: String = "Max", var age: Int = 30)

fun Route.persons() {

    route("/persons") {

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
