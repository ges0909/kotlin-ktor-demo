package de.schrader.ktor.api.repository

import de.schrader.ktor.api.model.Person
import de.schrader.ktor.common.CrudRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

interface PersonRepository : CrudRepository<Person, Int>

private const val MAX_NAME_COLUMN_LENGTH = 32
private const val MAX_USER_ID_COLUMN_LENGTH = 20

class PersonRepositoryImpl : PersonRepository {

    private object Persons : Table("PERSON") {
        val id = integer("id").autoIncrement().primaryKey()
        val userId = varchar("userId", MAX_USER_ID_COLUMN_LENGTH).primaryKey()
        val name = varchar("name", MAX_NAME_COLUMN_LENGTH)
        val age = integer("age")
    }

    override fun createTable() = transaction {
        SchemaUtils.create(Persons)
    }

    override fun dropTable() = transaction {
        SchemaUtils.drop(Persons)
    }

    override suspend fun findById(id: Int): Person? = suspendableTransaction {
        Persons.select { Persons.id eq id }
            .mapNotNull { it.toPerson() }
            .firstOrNull()
    }

    override suspend fun create(entity: Person): Int = suspendableTransaction {
        Persons.insert(entity.toRow()) get Persons.id
    }

    override suspend fun update(id: Int, entity: Person): Int = suspendableTransaction {
        Persons.update(where = { Persons.id eq id }) {
            it[name] = entity.name
            it[age] = entity.age
        }
    }

    override suspend fun delete(id: Int): Int = suspendableTransaction {
        Persons.deleteWhere { Persons.id eq id }
    }

    override suspend fun findAll(): List<Person> = suspendableTransaction {
        Persons.selectAll().map { it.toPerson() }
    }

    override suspend fun deleteAll(): Int = suspendableTransaction {
        Persons.deleteAll()
    }

    private fun ResultRow.toPerson() = Person(
        id = this[Persons.id],
        userId = this[Persons.userId],
        name = this[Persons.name],
        age = this[Persons.age]
    )

    private fun Person.toRow(): Persons.(UpdateBuilder<*>) -> Unit = {
        it[userId] = this@toRow.userId
        it[name] = this@toRow.name
        it[age] = this@toRow.age
    }
}
