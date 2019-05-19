package de.schrader.ktor.repository

import de.schrader.ktor.None
import de.schrader.ktor.Some
import de.schrader.ktor.Thing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class PersonRepository {

    object Schema : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", 32)
        val age = integer("age")
    }

    suspend fun all(): Thing<List<Person>> = withContext(Dispatchers.IO) {
        transaction {
            val query = Schema.selectAll()
            when {
                query.none() -> Some(emptyList())
                else -> Some(query.map { it.toPerson() })
            }
        }
    }

    suspend fun create(person: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            Schema.insert {
                it[name] = person.name
                it[age] = person.age
            } get Schema.id
        }
    }

    suspend fun read(id: Int): Thing<Person> = withContext(Dispatchers.IO) {
        transaction {
            val query = Schema.select { Schema.id eq id }
            when {
                query.none() -> None<Person>()
                else -> Some(query.first().toPerson())
            }
        }
    }

    suspend fun update(id: Int, person: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            Schema.update({ Schema.id eq id }) {
                it[name] = person.name
                it[age] = person.age
            }
        }
    }

    suspend fun delete(id: Int): Int = withContext(Dispatchers.IO) {
        transaction {
            Schema.deleteWhere { Schema.id eq id }
        }
    }

    private fun ResultRow.toPerson() = Person(
        id = this[Schema.id],
        name = this[Schema.name],
        age = this[Schema.age]
    )
}
