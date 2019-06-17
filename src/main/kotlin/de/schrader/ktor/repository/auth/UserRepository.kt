package de.schrader.ktor.repository.auth

import arrow.core.Option
import de.schrader.ktor.model.auth.User
import de.schrader.ktor.repository.common.CrudRepository
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

interface UserRepository : CrudRepository<User, Int>

class UserRepositoryImpl : UserRepository {

    private object Users : Table("USER") {
        val userId = varchar("id", 20).primaryKey()
        val email = varchar("email", 120).uniqueIndex()
        val displayName = varchar("display_name", 256)
        val passwordHash = varchar("password_hash", 64)
    }

    override fun createTable() = transaction {
        SchemaUtils.create(Users)
    }

    override fun dropTable() = transaction {
        SchemaUtils.drop(Users)
    }

    override suspend fun find(id: Int): Option<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun findAll(): List<User> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun create(entity: User): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun update(id: Int, entity: User): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun delete(id: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun deleteAll(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}