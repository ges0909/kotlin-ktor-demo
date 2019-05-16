package de.schrader.ktor.service

import de.schrader.ktor.controller.Person
import de.schrader.ktor.repository.PersonRepository

interface PersonService {
    fun all(): List<Person>
    fun get(id: Int): Person
    fun create(person: Person): Person
    fun update(id: Int, person: Person)
    fun delete(id: Int)
}

class PersonServiceImpl(private val personRepository: PersonRepository) : PersonService {

    override fun all(): List<Person> = personRepository.all()

    override fun get(id: Int): Person = personRepository.get(id)

    override fun create(person: Person): Person {
        val id = personRepository.create(person)
        return get(id)
    }

    override fun update(id: Int, person: Person) = personRepository.update(id, person)

    override fun delete(id: Int) = personRepository.delete(id)
}
