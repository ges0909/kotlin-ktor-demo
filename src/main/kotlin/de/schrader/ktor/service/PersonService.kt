package de.schrader.ktor.service

import arrow.core.Option
import de.schrader.ktor.repository.entity.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    suspend fun findAll(): Option<List<Person>>
    suspend fun create(person: Person): Option<Person>
    suspend fun find(id: Int): Option<Person>
    suspend fun update(id: Int, person: Person): Int
    suspend fun delete(id: Int): Int
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override suspend fun findAll(): Option<List<Person>> = personRepository.findAll()

    override suspend fun create(person: Person): Option<Person> {
        val id = personRepository.create(person)
        return find(id)
    }

    override suspend fun find(id: Int): Option<Person> = personRepository.find(id)

    override suspend fun update(id: Int, person: Person): Int = personRepository.update(id, person)

    override suspend fun delete(id: Int): Int = personRepository.delete(id)
}
