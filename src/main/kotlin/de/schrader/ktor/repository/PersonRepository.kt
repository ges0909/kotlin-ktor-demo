package de.schrader.ktor.repository

import de.schrader.ktor.controller.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PersonRepository {

    object DatabaseTable : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", 32)
        val age = integer("age")
    }

    suspend fun all(): List<Person> = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.selectAll().map { it.toPerson() }
        }
    }

    suspend fun get(id: Int): Person = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.select { DatabaseTable.id eq id }.first().toPerson()
        }
    }

    suspend fun create(person: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.insert {
                it[name] = person.name
                it[age] = person.age
            } get DatabaseTable.id
        }
    }

    suspend fun update(id: Int, person: Person) = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.update({ DatabaseTable.id eq id }) {
                it[name] = person.name
                it[age] = person.age
            }
            Unit
        }
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.deleteWhere { DatabaseTable.id eq id }
            Unit
        }
    }

    private fun ResultRow.toPerson() = Person(
        name = this[DatabaseTable.name],
        age = this[DatabaseTable.age]
    )
}
