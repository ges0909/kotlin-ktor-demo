package de.schrader.ktor.service

import de.schrader.ktor.model.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    suspend fun find(id: Int): Person?
    suspend fun create(person: Person): Person?
    suspend fun update(id: Int, person: Person): Int
    suspend fun delete(id: Int): Int
    suspend fun findAll(): List<Person>
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun find(id: Int): Person? = personRepository.findById(id)

    override suspend fun create(person: Person): Person? {
        val id = personRepository.create(person)
        return find(id)
    }

    override suspend fun update(id: Int, person: Person): Int = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)

    override suspend fun findAll(): List<Person> = personRepository.findAll()
}
