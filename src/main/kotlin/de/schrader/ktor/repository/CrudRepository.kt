package de.schrader.ktor.repository

import arrow.core.Option

interface CrudRepository<E, I> {
    fun createTable()
    suspend fun findAll(): Option<List<E>>
    suspend fun find(id: I): Option<E>
    suspend fun create(entity: E): I
    suspend fun update(id: I, entity: E): I
    suspend fun delete(id: I): I
}
