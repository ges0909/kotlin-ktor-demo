package de.schrader.ktor.repository

import arrow.core.Option

interface CrudRepository<T, I> {
    fun createTable()
    suspend fun findAll(): Option<List<T>>
    suspend fun find(id: I): Option<T>
    suspend fun create(entity: T): I
    suspend fun update(id: I, entity: T): I
    suspend fun delete(id: I): I
}
