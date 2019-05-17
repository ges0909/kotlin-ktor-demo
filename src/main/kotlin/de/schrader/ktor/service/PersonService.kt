package de.schrader.ktor.service

import de.schrader.ktor.Person
import de.schrader.ktor.Thing
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    suspend fun all(): List<Person>
    suspend fun create(person: Person): Thing<Person>
    suspend fun read(id: Int): Thing<Person>
    suspend fun update(id: Int, person: Person)
    suspend fun delete(id: Int): Int
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun all(): List<Person> = personRepository.all()

    override suspend fun create(person: Person): Thing<Person> {
        val id = personRepository.create(person)
        return read(id)
    }

    override suspend fun read(id: Int): Thing<Person> = personRepository.read(id)

    override suspend fun update(id: Int, person: Person) = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)
}
