package de.schrader.ktor.repository.auth

import de.schrader.ktor.model.auth.User
import de.schrader.ktor.repository.common.CrudRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

private const val MAX_USER_ID_COLUMN_LENGTH = 20
private const val MAX_EMAIL_COLUMN_LENGTH = 120
private const val MAX_DISPLAY_NAME_COLUMN_LENGTH = 256
private const val MAX_PASSWORD_HASH_COLUMN_LENGTH = 64

interface UserRepository : CrudRepository<User, String> {
    suspend fun findByIdAndHash(userId: String, hash: String): User?
    suspend fun findByEmail(email: String): User?
}

class UserRepositoryImpl : UserRepository {

    private object Users : Table("USER") {
        val userId = varchar("userId", MAX_USER_ID_COLUMN_LENGTH).primaryKey()
        val email = varchar("email", MAX_EMAIL_COLUMN_LENGTH).uniqueIndex()
        val displayName = varchar("display_name", MAX_DISPLAY_NAME_COLUMN_LENGTH)
        val passwordHash = varchar("password_hash", MAX_PASSWORD_HASH_COLUMN_LENGTH)
    }

    override fun createTable() = transaction {
        SchemaUtils.create(Users)
    }

    override fun dropTable() = transaction {
        SchemaUtils.drop(Users)
    }

    override suspend fun findById(id: String): User? = suspendableTransaction {
        Users.select { Users.userId eq id }
            .mapNotNull { it.toUser() }
            .firstOrNull()
    }

    override suspend fun create(entity: User): String = suspendableTransaction {
        Users.insert(entity.toRow()) get Users.userId
    }

    override suspend fun update(id: String, entity: User): Int = suspendableTransaction {
        Users.update(where = { Users.userId eq id }) {
            it[email] = entity.email
            it[displayName] = entity.displayName
            it[passwordHash] = entity.passwordHash
        }
    }

    override suspend fun delete(id: String): Int = suspendableTransaction {
        Users.deleteWhere { Users.userId eq id }
    }

    override suspend fun findAll(): List<User> = suspendableTransaction {
        Users.selectAll().map { it.toUser() }
    }

    override suspend fun deleteAll(): Int = suspendableTransaction {
        Users.deleteAll()
    }

    override suspend fun findByIdAndHash(userId: String, hash: String): User? = suspendableTransaction {
        Users.select { (Users.userId eq userId) and (Users.passwordHash eq hash) }
            .mapNotNull { it.toUser() }
            .firstOrNull()
    }

    override suspend fun findByEmail(email: String): User? = suspendableTransaction {
        Users.select { Users.email eq email }
            .mapNotNull { it.toUser() }
            .firstOrNull()
    }

    private fun ResultRow.toUser() = User(
        userId = this[Users.userId],
        email = this[Users.email],
        displayName = this[Users.displayName],
        passwordHash = this[Users.passwordHash]
    )

    private fun User.toRow(): Users.(UpdateBuilder<*>) -> Unit = {
        it[userId] = this@toRow.userId
        it[email] = this@toRow.email
        it[displayName] = this@toRow.displayName
        it[passwordHash] = this@toRow.passwordHash
    }
}
