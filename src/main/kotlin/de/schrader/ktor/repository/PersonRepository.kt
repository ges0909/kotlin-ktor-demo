package de.schrader.ktor.repository

import arrow.core.Option
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

private const val MAX_NAME_LENGTH: Int = 32

interface PersonRepository : CrudRepository<Person, Int>

class PersonRepositoryImpl : PersonRepository {

    private object Persons : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val name = varchar("name", MAX_NAME_LENGTH)
        val age = integer("age")
    }

    override fun createTable() = SchemaUtils.create(Persons)

    override suspend fun findAll(): Option<List<Person>> = withContext(Dispatchers.IO) {
        transaction {
            val query = Persons.selectAll()
            when {
                query.none() -> Option.empty()
                else -> Option.just(query.map { it.toPerson() })
            }
        }
    }

    override suspend fun find(id: Int): Option<Person> = withContext(Dispatchers.IO) {
        transaction {
            val query = Persons.select { Persons.id eq id }
            when {
                query.none() -> Option.empty()
                else -> Option.just(query.first().toPerson())
            }
        }
    }

    override suspend fun create(entity: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            Persons.insert {
                it[name] = entity.name
                it[age] = entity.age
            } get Persons.id
        }
    }

    override suspend fun update(id: Int, entity: Person): Int = withContext(Dispatchers.IO) {
        transaction {
            Persons.update({ Persons.id eq id }) {
                it[name] = entity.name
                it[age] = entity.age
            }
        }
    }

    override suspend fun delete(id: Int): Int = withContext(Dispatchers.IO) {
        transaction {
            Persons.deleteWhere { Persons.id eq id }
        }
    }

    private fun ResultRow.toPerson() = Person(
        id = this[Persons.id],
        name = this[Persons.name],
        age = this[Persons.age]
    )
}
