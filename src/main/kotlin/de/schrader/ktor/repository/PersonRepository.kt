package de.schrader.ktor.repository

import de.schrader.ktor.None
import de.schrader.ktor.Some
import de.schrader.ktor.Thing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PersonRepository {

    object PersonTable : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", 32)
        val age = integer("age")
    }

    suspend fun all(): Thing<List<Person>> = withContext(Dispatchers.IO) {
        transaction {
            val query = PersonTable.selectAll()
            when {
                query.none() -> Some(emptyList())
                else -> Some(query.map { it.toPerson() })
            }
        }
    }

    suspend fun create(person: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            PersonTable.insert {
                it[name] = person.name
                it[age] = person.age
            } get PersonTable.id
        }
    }

    suspend fun read(id: Int): Thing<Person> = withContext(Dispatchers.IO) {
        transaction {
            val query = PersonTable.select { PersonTable.id eq id }
            when {
                query.none() -> None<Person>()
                else -> Some(query.first().toPerson())
            }
        }
    }

    suspend fun update(id: Int, person: Person) = withContext(Dispatchers.IO) {
        transaction {
            PersonTable.update({ PersonTable.id eq id }) {
                it[name] = person.name
                it[age] = person.age
            }
            Unit
        }
    }

    suspend fun delete(id: Int): Int = withContext(Dispatchers.IO) {
        transaction {
            PersonTable.deleteWhere { PersonTable.id eq id }
        }
    }

    private fun ResultRow.toPerson() = Person(
        id = this[PersonTable.id],
        name = this[PersonTable.name],
        age = this[PersonTable.age]
    )
}
