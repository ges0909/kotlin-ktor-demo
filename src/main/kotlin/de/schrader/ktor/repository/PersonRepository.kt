package de.schrader.ktor.repository

import arrow.core.Option
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

private const val MAX_NAME_LENGTH: Int = 32

class PersonRepository {

    object Schema : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", MAX_NAME_LENGTH)
        val age = integer("age")
    }

    suspend fun all(): Option<List<Person>> = withContext(Dispatchers.IO) {
        transaction {
            val query = Schema.selectAll()
            when {
                query.none() -> Option.empty()
                else -> Option.just(query.map { it.toPerson() })
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

    suspend fun read(id: Int): Option<Person> = withContext(Dispatchers.IO) {
        transaction {
            val query = Schema.select { Schema.id eq id }
            when {
                query.none() -> Option.empty()
                else -> Option.just(query.first().toPerson())
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
