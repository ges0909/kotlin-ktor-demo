package de.schrader.ktor.service

import de.schrader.ktor.controller.Person
import de.schrader.ktor.repository.PersonRepository
import kotlinx.coroutines.withContext

interface PersonService {
    suspend fun all(): List<Person>
    suspend fun get(id: Int): Person
    suspend fun create(person: Person): Person
    suspend fun update(id: Int, person: Person)
    suspend fun delete(id: Int)
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun all(): List<Person> = personRepository.all()

    override suspend fun get(id: Int): Person = personRepository.get(id)

    override suspend fun create(person: Person): Person {
        val id = personRepository.create(person)
        return get(id)
    }

    override suspend fun update(id: Int, person: Person) = personRepository.update(id, person)

    override suspend fun delete(id: Int) = personRepository.delete(id)
}
