package de.schrader.ktor.repository

import de.schrader.ktor.None
import de.schrader.ktor.Person
import de.schrader.ktor.Some
import de.schrader.ktor.Thing
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

    suspend fun create(person: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.insert {
                it[name] = person.name
                it[age] = person.age
            } get DatabaseTable.id
        }
    }

    suspend fun read(id: Int): Thing<Person> = withContext(Dispatchers.IO) {
        transaction {
            val rows = DatabaseTable.select { DatabaseTable.id eq id }
            when {
                rows.empty() -> None<Person>()
                else -> Some(rows.first().toPerson())
            }
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

    suspend fun delete(id: Int): Int = withContext(Dispatchers.IO) {
        transaction {
            DatabaseTable.deleteWhere { DatabaseTable.id eq id }
        }
    }

    private fun ResultRow.toPerson() = Person(
        id = this[DatabaseTable.id],
        name = this[DatabaseTable.name],
        age = this[DatabaseTable.age]
    )
}
