package de.schrader.ktor.routes

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object PersonTable : Table("PERSON") {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 32)
    val age = integer("age")
}

private fun PersonTable.getAll(): List<Person> = transaction {
    PersonTable.selectAll().map { it.toPerson() }
}

private fun PersonTable.getById(id: Int): Person = transaction {
    PersonTable.select { PersonTable.id eq id }.first().toPerson()
}

private fun PersonTable.create(person: Person): Person = transaction {
    val id = PersonTable.insert {
        it[name] = person.name
        it[age] = person.age
    } get PersonTable.id
    PersonTable.select { PersonTable.id eq id }.first().toPerson()
}

private fun PersonTable.update(person: Person): Person = transaction {
    PersonTable.update({ PersonTable.id eq person.id }) {
        it[name] = person.name
        it[age] = person.age
    }
    person
}

private fun PersonTable.delete(id: Int) = transaction {
    PersonTable.deleteWhere { PersonTable.id eq id }
}

private fun ResultRow.toPerson() = Person(
    id = this[PersonTable.id],
    name = this[PersonTable.name],
    age = this[PersonTable.age]
)

private data class Person(val id: Int, val name: String, var age: Int)

fun Route.persons() {
    route("/persons") {
        get {
            call.respond(HttpStatusCode.OK, PersonTable.getAll())
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            call.respond(HttpStatusCode.OK, PersonTable.getById(id))
        }

        post {
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.Created, PersonTable.create(person))
        }

        put {
            val person = call.receive<Person>()
            call.respond(HttpStatusCode.OK, PersonTable.update(person))
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            PersonTable.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
