package de.schrader.ktor.repository.common

import arrow.core.Option
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

interface CrudRepository<E, I> {
    fun createTable()
    fun dropTable()
    suspend fun find(id: I): Option<E>
    suspend fun findAll(): List<E>
    suspend fun create(entity: E): I
    suspend fun update(id: I, entity: E): Int
    suspend fun delete(id: I): Int
    suspend fun deleteAll(): Int
    suspend fun <T> suspendableTransaction(dbStmt: () -> T): T = withContext(Dispatchers.IO) {
        transaction { dbStmt() }
    }
}
