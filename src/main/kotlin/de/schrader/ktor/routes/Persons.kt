package de.schrader.ktor.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Persons : Table("PERSON") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 32)
    val age = integer("age")
}

private fun Persons.getAll(): List<Person> = transaction {
    Persons.selectAll().map { it.toPerson() }
}

private fun Persons.getById(id: Int): Person = transaction {
    Persons.select { Persons.id eq id }.first().toPerson()
}

private fun Persons.create(person: Person): Person = transaction {
    val id = Persons.insert {
        it[name] = person.name
        it[age] = person.age
    } get Persons.id
    Persons.select { Persons.id eq id }.first().toPerson()
}

private fun Persons.update(id: Int, person: Person): Person = transaction {
    Persons.update({ Persons.id eq id }) {
        it[name] = person.name
        it[age] = person.age
    }
    person
}

private fun Persons.delete(id: Int) = transaction {
    Persons.deleteWhere { Persons.id eq id }
}

private fun ResultRow.toPerson() = Person(
    name = this[Persons.name],
    age = this[Persons.age]
)

data class Person(val name: String, var age: Int)

fun Route.persons() {

    route("/persons") {

        get {
            call.respond(HttpStatusCode.OK, Persons.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            call.respond(HttpStatusCode.OK, Persons.getById(id))
        }

        post {
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.Created, Persons.create(person))
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.OK, Persons.update(id, person))
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            Persons.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
