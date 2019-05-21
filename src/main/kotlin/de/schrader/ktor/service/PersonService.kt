package de.schrader.ktor.service

import arrow.core.Option
import de.schrader.ktor.repository.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    suspend fun all(): Option<List<Person>>
    suspend fun create(person: Person): Option<Person>
    suspend fun read(id: Int): Option<Person>
    suspend fun update(id: Int, person: Person): Int
    suspend fun delete(id: Int): Int
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun all(): Option<List<Person>> = personRepository.all()

    override suspend fun create(person: Person): Option<Person> {
        val id = personRepository.create(person)
        return read(id)
    }

    override suspend fun read(id: Int): Option<Person> = personRepository.read(id)

    override suspend fun update(id: Int, person: Person): Int = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)
}
