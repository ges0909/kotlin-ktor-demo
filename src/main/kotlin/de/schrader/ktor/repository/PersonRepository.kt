package de.schrader.ktor.repository

import de.schrader.ktor.route.Person
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PersonRepository {

    object DatabaseTable : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", 32)
        val age = integer("age")
    }

    fun get(): List<Person> = transaction {
        DatabaseTable.selectAll().map { it.toPerson() }
    }

    fun getById(id: Int): Person = transaction {
        DatabaseTable.select { DatabaseTable.id eq id }.first().toPerson()
    }

    fun create(person: Person): Int = transaction {
        DatabaseTable.insert {
            it[name] = person.name
            it[age] = person.age
        } get DatabaseTable.id
    }

    fun update(id: Int, person: Person) = transaction {
        DatabaseTable.update({ DatabaseTable.id eq id }) {
            it[name] = person.name
            it[age] = person.age
        }
        Unit
    }

    fun delete(id: Int) = transaction {
        DatabaseTable.deleteWhere { DatabaseTable.id eq id }
        Unit
    }

    private fun ResultRow.toPerson() = Person(
        name = this[DatabaseTable.name],
        age = this[DatabaseTable.age]
    )
}
