package de.schrader.ktor.service

import de.schrader.ktor.Thing
import de.schrader.ktor.repository.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    suspend fun all(): Thing<List<Person>>
    suspend fun create(person: Person): Thing<Person>
    suspend fun read(id: Int): Thing<Person>
    suspend fun update(id: Int, person: Person): Int
    suspend fun delete(id: Int): Int
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun all(): Thing<List<Person>> = personRepository.all()

    override suspend fun create(person: Person): Thing<Person> {
        val id = personRepository.create(person)
        return read(id)
    }

    override suspend fun read(id: Int): Thing<Person> = personRepository.read(id)

    override suspend fun update(id: Int, person: Person): Int = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)
}
