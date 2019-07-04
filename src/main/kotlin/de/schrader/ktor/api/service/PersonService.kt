package de.schrader.ktor.api.service

import de.schrader.ktor.api.model.Person
import de.schrader.ktor.api.repository.PersonRepository

interface PersonService {
    suspend fun findById(id: Int): Person?
    suspend fun findAll(): List<Person>
    suspend fun create(person: Person): Person?
    suspend fun update(id: Int, person: Person): Int
    suspend fun delete(id: Int): Int
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun findById(id: Int): Person? = personRepository.findById(id)

    override suspend fun findAll(): List<Person> = personRepository.findAll()

    override suspend fun create(person: Person): Person? {
        val id = personRepository.create(person)
        return findById(id)
    }

    override suspend fun update(id: Int, person: Person): Int = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)
}
