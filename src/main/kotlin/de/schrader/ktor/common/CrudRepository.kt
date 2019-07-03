package de.schrader.ktor.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.transaction

interface CrudRepository<E, I> {
    fun createTable()
    fun dropTable()
    suspend fun findById(id: I): E?
    suspend fun create(entity: E): I
    suspend fun update(id: I, entity: E): Int
    suspend fun delete(id: I): Int
    suspend fun findAll(): List<E>
    suspend fun deleteAll(): Int
    suspend fun <T> suspendableTransaction(sql: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { sql() }
        }
}
